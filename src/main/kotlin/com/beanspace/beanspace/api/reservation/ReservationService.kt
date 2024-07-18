package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.exception.AccessDeniedException
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ReservationService(
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val memberRepository: MemberRepository,
    private val couponRepository: CouponRepository
) {

    @Transactional
    fun reserveSpace(principal: UserPrincipal, spaceId: Long, request: ReservationRequest): ReservationResponse {
        val space = spaceRepository.findByIdOrNull(spaceId) // space active 한지 확인하는 부분 추가 예정
            ?: throw ModelNotFoundException("공간", spaceId)

        val member = memberRepository.findByIdOrNull(principal.id)
            ?: throw ModelNotFoundException("사용자", principal.id)

        val coupon = couponRepository.findByIdOrNull(request.couponId)
            ?: throw ModelNotFoundException("쿠폰", request.couponId)

        check(request.checkIn < request.checkOut) { throw IllegalArgumentException("체크인, 체크아웃 날짜가 올바른지 확인해주세요.") }

        // 체크 아웃 날짜가 오늘로부터 6개월 뒤까지만 가능
        check(request.checkOut.isBefore(LocalDate.now().plusMonths(6)))
        { throw IllegalArgumentException("예약이 가능한 날짜인지 확인해주세요.") }

        check(request.reservationPeople in 1..space.maxPeople)
        { throw IllegalArgumentException("예약 인원이 올바른지 확인해주세요.") }

        // 해당 날짜에 예약이 가능한가?
        check(isReservationPossible(space.id!!, request.checkIn, request.checkOut))
        { throw IllegalArgumentException("예약이 이미 완료된 날짜입니다.") }

        // 쿠폰이 유효한가?
        check(coupon.expirationAt.isBefore(LocalDateTime.now()))
        { throw IllegalStateException("쿠폰의 유효기간을 확인해주세요.") }

        // 숙박일수
        val stayDays = ChronoUnit.DAYS.between(request.checkIn, request.checkOut)

        // 기본 가격
        val regularPrice = stayDays * space.price

        // 추가 인원 가격
        val extraPersonCharge = ((request.reservationPeople - space.defaultPeople) * space.price).coerceAtLeast(0)

        // 쿠폰 적용 전 가격
        val totalPriceBeforeCoupon = regularPrice + extraPersonCharge

        // 쿠폰 할인 가격
        val couponDiscountedPrice =
            (totalPriceBeforeCoupon * coupon.discountRate / 100).coerceAtMost(coupon.maxDiscount.toLong())

        // 최종 가격
        val finalPrice = totalPriceBeforeCoupon - couponDiscountedPrice

        return request.toEntity(space, member, finalPrice)
            .also { reservationRepository.save(it) }
            .let { ReservationResponse.from(it) }
    }

    @Transactional
    fun cancelReservation(principal: UserPrincipal, reservationId: Long) {
        reservationRepository.findByIdOrNull(reservationId)
            ?.also { check(it.validateOwner(principal.id)) { throw AccessDeniedException("본인이 한 예약인지 확인해주세요.") } }
            ?.also { check(!it.isCancellationDeadlinePassed()) { throw IllegalStateException("예약 취소 가능 날짜가 지났습니다.") } }
            ?.also { check(!it.isCancelledReservation()) { throw IllegalStateException("예약 취소가 가능한 상태가 아닙니다.") } }
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