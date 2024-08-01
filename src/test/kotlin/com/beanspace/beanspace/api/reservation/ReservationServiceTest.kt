package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.domain.coupon.model.Coupon
import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate
import java.time.LocalDateTime

class ReservationServiceTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk()
    val reservationRepository: ReservationRepository = mockk()
    val memberRepository: MemberRepository = mockk()
    val userCouponRepository: UserCouponRepository = mockk()
    val redissonClient: RedissonClient = mockk()
    val transactionTemplate: TransactionTemplate = mockk()

    val reservationService = spyk(
        ReservationService(
            spaceRepository = spaceRepository,
            reservationRepository = reservationRepository,
            memberRepository = memberRepository,
            userCouponRepository = userCouponRepository,
            redissonClient = redissonClient,
            transactionTemplate = transactionTemplate
        ),
        recordPrivateCalls = true
    )

    beforeContainer {
        clearAllMocks()

        val lock = mockk<RLock> {
            every { tryLock(any(), any(), any()) } returns true
            every { isHeldByCurrentThread } returns true
            every { unlock() } just Runs
        }

        every { redissonClient.getLock(any<String>()) } returns lock

        every { transactionTemplate.execute(any<TransactionCallback<ReservationResponse>>()) } answers {
            val callback = firstArg<TransactionCallback<ReservationResponse>>()
            callback.doInTransaction(mockk())
        }
    }

    context("ReservationService.reserveSpace()") {
        given("예약하려는 공간이") {
            `when`("예약자가 제공하는 공간이면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns guest
                    }
                    val request = mockk<ReservationRequest> { every { userCouponId } returns null }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("체크인 날짜가") {
            `when`("당일이면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                    }
                    val request = mockk<ReservationRequest> {
                        every { userCouponId } returns null
                        every { checkIn } returns LocalDate.now()
                        every { checkOut } returns LocalDate.now().plusDays(1)
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("체크아웃 날짜가") {
            `when`("체크인 날짜 이전이면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                    }
                    val request = mockk<ReservationRequest> {
                        every { userCouponId } returns null
                        every { checkIn } returns LocalDate.now().plusDays(5)
                        every { checkOut } returns LocalDate.now().plusDays(1)
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }

            `when`("당일로부터 6개월 이후면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                    }
                    val request = mockk<ReservationRequest> {
                        every { userCouponId } returns null
                        every { checkIn } returns LocalDate.now().plusDays(1)
                        every { checkOut } returns LocalDate.now().plusMonths(6)
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("예약하려는 날짜가") {
            `when`("이미 예약이 되어있으면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                        every { maxPeople } returns 6
                    }
                    val request = mockk<ReservationRequest> {
                        every { userCouponId } returns null
                        every { checkIn } returns LocalDate.now().plusDays(1)
                        every { checkOut } returns LocalDate.now().plusDays(5)
                        every { reservationPeople } returns 2
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every {
                        reservationService["isReservationPossible"](
                            1L,
                            any<LocalDate>(),
                            any<LocalDate>()
                        )
                    } returns false

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("예약인원이") {
            `when`("공간의 기본 인원수와 최대 인원수 사이가 아니면") {
                then("IllegalArgumentException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }

                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                        every { defaultPeople } returns 2
                        every { maxPeople } returns 4
                    }
                    val request = mockk<ReservationRequest> {
                        every { userCouponId } returns null
                        every { checkIn } returns LocalDate.now().plusDays(1)
                        every { checkOut } returns LocalDate.now().plusDays(5)
                        every { reservationPeople } returns 6
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest

                    shouldThrow<IllegalArgumentException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("사용하려는 쿠폰이") {
            `when`("이미 사용된 쿠폰이라면") {
                then("IllegalStateException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> { every { id } returns 1L }
                    val request = mockk<ReservationRequest> { every { userCouponId } returns 1L }
                    val userCoupon = mockk<UserCoupon> { every { isCouponUnused() } returns false }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every { userCouponRepository.findByIdOrNull(1L) } returns userCoupon

                    shouldThrow<IllegalStateException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }

            `when`("유효기간이 지난 쿠폰이라면") {
                then("IllegalStateException 발생") {
                    val guest = mockk<Member> { every { id } returns 1L }
                    val space = mockk<Space> { every { id } returns 1L }
                    val request = mockk<ReservationRequest> { every { userCouponId } returns 1L }
                    val userCoupon = mockk<UserCoupon> {
                        every { isCouponUnused() } returns true
                        every { coupon } returns mockk<Coupon> {
                            every { isNotExpired() } returns false
                        }
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every { userCouponRepository.findByIdOrNull(1L) } returns userCoupon

                    shouldThrow<IllegalStateException> {
                        reservationService.reserveSpace(1L, 1L, request)
                    }
                }
            }
        }

        given("기본인원: 2명, 기본 요금: 30000원, 추가요금: 10000원인 공간을 4명이 2박을 예약할때") {
            `when`("적용한 쿠폰이 없다면") {
                then("결제금액은 (30000 + 10000 x 2) x 2 = 100000원 이어야한다.") {
                    val guest = mockk<Member> { every { id } returns 1L }

                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                        every { defaultPeople } returns 2
                        every { maxPeople } returns 4
                        every { price } returns 30000
                        every { pricePerPerson } returns 10000
                        every { createdAt } returns LocalDateTime.now()
                        every { listingName } returns "asdasdasd"
                        every { calculateTotalCost(any(), any()) } answers { 100000 }
                    }

                    val request = ReservationRequest(
                        checkIn = LocalDate.now().plusDays(1),
                        checkOut = LocalDate.now().plusDays(3),
                        reservationPeople = 4,
                        userCouponId = null
                    )

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every {
                        reservationService["isReservationPossible"](
                            1L,
                            any<LocalDate>(),
                            any<LocalDate>()
                        )
                    } returns true

                    every { reservationRepository.save(any<Reservation>()) } answers {
                        val arg = firstArg<Reservation>()
                        Reservation(
                            id = 1L,
                            checkIn = arg.checkIn,
                            checkOut = arg.checkOut,
                            reservationPeople = arg.reservationPeople,
                            cost = arg.cost,
                            createdAt = LocalDateTime.now(),
                            space = arg.space,
                            member = arg.member
                        )
                    }

                    val result = reservationService.reserveSpace(1L, 1L, request)

                    result.cost shouldBe 100000
                }
            }

            `when`("할인율이 30%, 최대할인 금액이 40000원인 쿠폰을 적용하면") {
                then("결제금액은 100000 - 30000 = 70000원 이어야 한다.") {
                    val guest = mockk<Member> { every { id } returns 1L }

                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                        every { defaultPeople } returns 2
                        every { maxPeople } returns 4
                        every { price } returns 30000
                        every { pricePerPerson } returns 10000
                        every { createdAt } returns LocalDateTime.now()
                        every { listingName } returns "asdasdasd"
                        every { calculateTotalCost(any(), any()) } answers { 100000 }
                    }

                    val request = ReservationRequest(
                        checkIn = LocalDate.now().plusDays(1),
                        checkOut = LocalDate.now().plusDays(3),
                        reservationPeople = 4,
                        userCouponId = 1L
                    )

                    val userCoupon = mockk<UserCoupon> {
                        every { isCouponUnused() } returns true
                        every { useCoupon() } returns Unit
                        every { coupon } returns mockk<Coupon> {
                            every { isNotExpired() } returns true
                            every { discountRate } returns 30
                            every { maxDiscount } returns 40000
                            every { calculateDiscountAmount(any()) } returns 30000
                        }
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every {
                        reservationService["isReservationPossible"](
                            1L,
                            any<LocalDate>(),
                            any<LocalDate>()
                        )
                    } returns true
                    every { userCouponRepository.findByIdOrNull(1L) } returns userCoupon
                    every { reservationRepository.save(any<Reservation>()) } answers {
                        val arg = firstArg<Reservation>()
                        Reservation(
                            id = 1L,
                            checkIn = arg.checkIn,
                            checkOut = arg.checkOut,
                            reservationPeople = arg.reservationPeople,
                            cost = arg.cost,
                            createdAt = LocalDateTime.now(),
                            space = arg.space,
                            member = arg.member
                        )
                    }

                    val result = reservationService.reserveSpace(1L, 1L, request)

                    result.cost shouldBe 70000
                }
            }

            `when`("할인율이 30%, 최대 할인 금액이 20000원인 쿠폰을 적용하면") {
                then("결제금액은 100000 - 20000 = 80000원 이어야한다.") {
                    val guest = mockk<Member> { every { id } returns 1L }

                    val space = mockk<Space> {
                        every { id } returns 1L
                        every { host } returns mockk<Member> { every { id } returns 2L }
                        every { defaultPeople } returns 2
                        every { maxPeople } returns 4
                        every { price } returns 30000
                        every { pricePerPerson } returns 10000
                        every { createdAt } returns LocalDateTime.now()
                        every { listingName } returns "asdasdasd"
                        every { calculateTotalCost(any(), any()) } answers { 100000 }
                    }

                    val request = ReservationRequest(
                        checkIn = LocalDate.now().plusDays(1),
                        checkOut = LocalDate.now().plusDays(3),
                        reservationPeople = 4,
                        userCouponId = 1L
                    )

                    val userCoupon = mockk<UserCoupon> {
                        every { isCouponUnused() } returns true
                        every { useCoupon() } returns Unit
                        every { coupon } returns mockk<Coupon> {
                            every { isNotExpired() } returns true
                            every { discountRate } returns 30
                            every { maxDiscount } returns 20000
                            every { calculateDiscountAmount(any()) } returns 20000
                        }
                    }

                    every { spaceRepository.findByIdAndStatus(1L, SpaceStatus.ACTIVE) } returns space
                    every { memberRepository.findByIdOrNull(1L) } returns guest
                    every {
                        reservationService["isReservationPossible"](
                            1L,
                            any<LocalDate>(),
                            any<LocalDate>()
                        )
                    } returns true
                    every { userCouponRepository.findByIdOrNull(1L) } returns userCoupon
                    every { reservationRepository.save(any<Reservation>()) } answers {
                        val arg = firstArg<Reservation>()
                        Reservation(
                            id = 1L,
                            checkIn = arg.checkIn,
                            checkOut = arg.checkOut,
                            reservationPeople = arg.reservationPeople,
                            cost = arg.cost,
                            createdAt = LocalDateTime.now(),
                            space = arg.space,
                            member = arg.member
                        )
                    }

                    val result = reservationService.reserveSpace(1L, 1L, request)

                    result.cost shouldBe 80000
                }
            }
        }
    }

    context("ReservationService.cancelReservation()") {
        given("예약 취소시") {
            `when`("본인이 한 예약이 아니면") {
                then("NoPermissionException 발생") {
                    val member = mockk<Member> { every { id } returns 1L }

                    every { reservationRepository.findByIdOrNull(1L) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns false
                    }

                    shouldThrow<NoPermissionException> {
                        reservationService.cancelReservation(member.id!!, 1L)
                    }
                }
            }

            `when`("예약 취소 가능 날짜가 지났으면") {
                then("IllegalStateException 발생") {
                    val member = mockk<Member> { every { id } returns 1L }

                    every { reservationRepository.findByIdOrNull(1L) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { isBeforeCancellationDeadline() } returns false
                    }

                    shouldThrow<IllegalStateException> {
                        reservationService.cancelReservation(member.id!!, 1L)
                    }
                }
            }

            `when`("이미 취소된 예약이면") {
                then("IllegalStateException 발생") {
                    val member = mockk<Member> { every { id } returns 1L }

                    every { reservationRepository.findByIdOrNull(1L) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { isBeforeCancellationDeadline() } returns true
                        every { isActiveReservation() } returns false
                    }

                    shouldThrow<IllegalStateException> {
                        reservationService.cancelReservation(member.id!!, 1L)
                    }
                }
            }
        }

        `when`("모든 조건을 만족하면") {
            then("예약이 취소된다.") {
                val member = mockk<Member> { every { id } returns 1L }

                val reservation = mockk<Reservation> {
                    every { validateOwner(1L) } returns true
                    every { isBeforeCancellationDeadline() } returns true
                    every { isActiveReservation() } returns true
                    every { cancelReservation() } returns Unit
                }

                every { reservationRepository.findByIdOrNull(1L) } returns reservation

                reservationService.cancelReservation(member.id!!, 1L)

                verify { reservation.cancelReservation() }
            }
        }
    }
})
