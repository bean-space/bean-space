package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Service
class ReservationService(
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val memberRepository: MemberRepository,
    private val userCouponRepository: UserCouponRepository,
    private val redissonClient: RedissonClient,
    private val transactionTemplate: TransactionTemplate
) {

    private val logger = LoggerFactory.getLogger(ReservationService::class.java)

    fun reserveSpace(guestId: Long, spaceId: Long, request: ReservationRequest): ReservationResponse {
        val key = "reservaion:$spaceId"
        val lock = redissonClient.getLock(key)

        var couponLock: RLock? = null

        try {
            val result = transactionTemplate.execute {

                if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) throw IllegalStateException("이미 예약중인 숙소입니다.")

                val space = spaceRepository.findByIdAndStatus(spaceId, SpaceStatus.ACTIVE)
                    ?: throw ModelNotFoundException("공간", spaceId)

                val guest = memberRepository.findByIdOrNull(guestId)
                    ?.also {
                        check(it.phoneNumber != "EMPTY") { throw IllegalStateException("전화번호를 입력하지 않은 회원은 예약이 불가능합니다.") }
                    } ?: throw ModelNotFoundException("사용자", guestId)

                val userCoupon = request.userCouponId?.let {
                    val couponKey = "userCoupon:$it"
                    couponLock = redissonClient.getLock(couponKey)

                    try {
                        if (!couponLock!!.tryLock(5, 10, TimeUnit.SECONDS)) {
                            throw IllegalStateException("이미 사용중인 쿠폰입니다.")
                        }

                        userCouponRepository.findByIdOrNull(it)
                            ?: throw ModelNotFoundException("UserCoupon", it)
                    } catch (e: RuntimeException) {
                        couponLock?.unlock()
                        couponLock = null
                        throw e
                    }
                }?.also {
                    check(it.isCouponUnused()) { throw IllegalStateException("이미 사용한 쿠폰입니다.") }
                }?.also {
                    check(it.coupon.isNotExpired()) { throw IllegalStateException("쿠폰의 유효기간을 확인해주세요.") }
                }

                // 자기 자신의 숙소는 예약 불가능
                check(space.host.id != guestId) { throw IllegalArgumentException("자기 자신의 공간은 예약할 수 없습니다.") }

                // 체크아웃 날짜가 체크인 날짜 이후인가?
                check(request.checkIn < request.checkOut) { throw IllegalArgumentException("체크인, 체크아웃 날짜가 올바른지 확인해주세요.") }

                // 체크인 날짜는 내일 이후로만 가능 (당일 예약 불가)
                check(request.checkIn.isAfter(LocalDate.now()))
                { throw IllegalArgumentException("예약이 가능한 날짜인지 확인해주세요.") }

                // 체크 아웃 날짜가 오늘로부터 6개월 뒤까지만 가능
                check(request.checkOut.isBefore(LocalDate.now().plusMonths(6)))
                { throw IllegalArgumentException("예약이 가능한 날짜인지 확인해주세요.") }

                // 예약 인원이 올바른가?
                check(request.reservationPeople in 1..space.maxPeople)
                { throw IllegalArgumentException("예약 인원이 올바른지 확인해주세요.") }

                // 해당 날짜에 예약이 가능한가?
                check(isReservationPossible(space.id!!, request.checkIn, request.checkOut))
                { throw IllegalArgumentException("예약이 이미 완료된 날짜입니다.") }

                // 숙박일수
                val stayDays = ChronoUnit.DAYS.between(request.checkIn, request.checkOut)

                // 결제 금액
                val cost = space.calculateTotalCost(request.reservationPeople, stayDays)
                    .let { it - (userCoupon?.coupon?.calculateDiscountAmount(it) ?: 0L) }

                // 쿠폰 사용 처리
                userCoupon?.useCoupon()

                request.toEntity(space, guest, cost)
                    .let { reservationRepository.save(it) }
                    .let { ReservationResponse.from(it) }
            }
            return result ?: throw IllegalStateException("예약 처리 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            logger.error(e.message, e)
            throw e
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
            if (couponLock?.isHeldByCurrentThread == true) {
                couponLock!!.unlock()
            }
        }
    }

    @Transactional
    fun cancelReservation(guestId: Long, reservationId: Long) {
        reservationRepository.findByIdOrNull(reservationId)
            ?.also { check(it.validateOwner(guestId)) { throw NoPermissionException("본인이 한 예약인지 확인해주세요.") } }
            ?.also { check(it.isBeforeCancellationDeadline(LocalDate.now())) { throw IllegalStateException("예약 취소 가능 날짜가 지났습니다.") } }
            ?.also { check(it.isActiveReservation()) { throw IllegalStateException("이미 취소된 예약입니다.") } }
            ?.cancelReservation()
            ?: throw ModelNotFoundException("예약", reservationId)
    }

    /* 특정 공간에 대해 예약할 수 없는 날짜를 구하는 함수
       지금은 쓸일이 없지만 나중에 쓸 수도 있을까봐 남겨두었음
    */
    private fun getUnavailableDatesForSpace(spaceId: Long): List<LocalDate> {
        val existingReservations =
            reservationRepository.findAllBySpaceIdAndIsCancelledAndCheckOutAfter(spaceId, false, LocalDate.now())

        val unavailableDates = mutableListOf<LocalDate>()

        for (reservation in existingReservations) {

            val existingReservationDates = reservation.checkIn.datesUntil(reservation.checkOut).toList()

            unavailableDates.addAll(existingReservationDates)
        }

        return unavailableDates
    }

    private fun isReservationPossible(spaceId: Long, checkIn: LocalDate, checkOut: LocalDate): Boolean {
        // 취소되지 않은, 체크아웃 날짜가 오늘 이후인 예약들만 조회
        val existingReservations =
            reservationRepository.findAllBySpaceIdAndIsCancelledAndCheckOutAfter(spaceId, false, LocalDate.now())

        // 예약의 날짜 범위 (예시 7/20 ~ 7/24 이면 [7/20,7/21,7/23])
        val newReservationDates = checkIn.datesUntil(checkOut).toList()

        for (reservation in existingReservations) {

            // 기존 예약의 날짜 범위
            val existingReservationDates = reservation.checkIn.datesUntil(reservation.checkOut).toList()

            // 새 예약 날짜와 기존 예약 날짜가 겹치는지 확인
            if (newReservationDates.intersect(existingReservationDates.toSet()).isNotEmpty()) {
                return false
            }
        }

        return true
    }
}