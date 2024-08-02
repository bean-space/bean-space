package com.beanspace.beanspace.api.reservation

import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.domain.coupon.model.Coupon
import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class ReservationConcurrencyTest @Autowired constructor(
    private val reservationService: ReservationService,
    private val reservationRepository: ReservationRepository,
    private val spaceRepository: SpaceRepository,
    private val memberRepository: MemberRepository,
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository
) : BehaviorSpec({

    afterTest {
        reservationRepository.deleteAll()
        spaceRepository.deleteAll()
        userCouponRepository.deleteAll()
        couponRepository.deleteAll()
        memberRepository.deleteAll()
    }

    context("ReservationService.reserveSpace()") {
        given("서로 다른 유저 10명이") {
            `when`("동시에 같은 체크인, 체크아웃 날짜에 같은 숙소를 예약하려고 할 때") {
                then("한 명만 성공하고 나머지는 IllegalArgumentException이 발생한다.") {
                    var exceptionCnt = 0
                    val exceptionList = mutableListOf<Exception>()

                    val member1 = memberRepository.saveAndFlush(getMember())
                    repeat(9) { memberRepository.saveAndFlush(getMember()) }

                    val host = memberRepository.saveAndFlush(getHost())

                    val space = spaceRepository.saveAndFlush(testSpace(host))

                    val checkIn = LocalDate.now().plusDays(5)
                    val checkOut = LocalDate.now().plusDays(8)

                    val reservationRequest = ReservationRequest(
                        checkIn = checkIn,
                        checkOut = checkOut,
                        reservationPeople = 1,
                        userCouponId = null
                    )

                    val executor = Executors.newFixedThreadPool(10)
                    val barrier = CyclicBarrier(10)

                    for (i in member1.id!!..member1.id!! + 9) {
                        executor.execute {
                            try {
                                barrier.await()
                                reservationService.reserveSpace(i, space.id!!, reservationRequest)
                            } catch (e: Exception) {
                                exceptionList.add(e)
                                exceptionCnt++
                            }
                        }
                    }

                    executor.shutdown()
                    executor.awaitTermination(10, TimeUnit.SECONDS)
                    println(host.id)

                    reservationRepository.findAll().size shouldBe 1
                    exceptionList.size shouldBe 9
                    exceptionList.forEach { it.shouldBeTypeOf<IllegalArgumentException>() }
                }
            }

            `when`("동시에 기간이 겹치는 체크인, 체크아웃 날짜에 같은 숙소를 예약하려고 할 때") {
                then("한 명만 성공하고 나머지는 IllegalArgumentException이 발생한다.") {
                    var exceptionCnt = 0
                    val exceptionList = mutableListOf<Exception>()

                    val member1 = memberRepository.saveAndFlush(getMember())
                    repeat(9) { memberRepository.saveAndFlush(getMember()) }

                    val host = memberRepository.saveAndFlush(getHost())

                    val space = spaceRepository.saveAndFlush(testSpace(host))

                    val checkIn = LocalDate.now().plusDays(2)
                    val checkOut = LocalDate.now().plusDays(20)

                    val executor = Executors.newFixedThreadPool(10)
                    val barrier = CyclicBarrier(10)

                    for (i in member1.id!!..member1.id!! + 9) {
                        executor.execute {
                            try {
                                barrier.await()
                                val reservationRequest = ReservationRequest(
                                    checkIn = checkIn.plusDays(i - member1.id!!),
                                    checkOut = checkOut.plusDays(i - member1.id!!),
                                    reservationPeople = 1,
                                    userCouponId = null
                                )
                                reservationService.reserveSpace(i, space.id!!, reservationRequest)
                            } catch (e: Exception) {
                                exceptionList.add(e)
                                exceptionCnt++
                            }
                        }
                    }

                    executor.shutdown()
                    executor.awaitTermination(10, TimeUnit.SECONDS)

                    reservationRepository.findAll().size shouldBe 1
                    exceptionList.size shouldBe 9
                    exceptionList.forEach { it.shouldBeTypeOf<IllegalArgumentException>() }
                }
            }

            `when`("같은 체크인, 체크아웃 날짜로 서로 다른 숙소를 예약하려고 할 때") {
                then("예약에 모두 성공해야한다.") {
                    var exceptionCnt = 0

                    val member1 = memberRepository.saveAndFlush(getMember())
                    repeat(9) { memberRepository.saveAndFlush(getMember()) }

                    val host = memberRepository.saveAndFlush(getHost())

                    val space1 = spaceRepository.saveAndFlush(testSpace(host))
                    repeat(9) { spaceRepository.saveAndFlush(testSpace(host)) }

                    val checkIn = LocalDate.now().plusDays(2)
                    val checkOut = LocalDate.now().plusDays(10)

                    val executor = Executors.newFixedThreadPool(10)
                    val barrier = CyclicBarrier(10)

                    for (i in member1.id!!..member1.id!! + 9) {
                        executor.execute {
                            try {
                                barrier.await()
                                val reservationRequest = ReservationRequest(
                                    checkIn = checkIn.plusDays(i),
                                    checkOut = checkOut.plusDays(i),
                                    reservationPeople = 1,
                                    userCouponId = null
                                )
                                reservationService.reserveSpace(i, space1.id!! + i - member1.id!!, reservationRequest)
                            } catch (e: Exception) {
                                exceptionCnt++
                            }
                        }
                    }

                    executor.shutdown()
                    executor.awaitTermination(10, TimeUnit.SECONDS)

                    reservationRepository.findAll().size shouldBe 10
                    exceptionCnt shouldBe 0
                }
            }
        }

        given("유저 1명이") {
            `when`("동시에 같은 쿠폰으로 서로 다른 숙소를 예약을 하려고 할 때") {
                then("한 예약만 성공하고 나머지는 IllegalStateException이 발생해야 한다.") {
                    val exceptionList = mutableListOf<Exception>()

                    val member = memberRepository.saveAndFlush(getMember())

                    val coupon = couponRepository.saveAndFlush(getCoupon())

                    val userCoupon = userCouponRepository.saveAndFlush(UserCoupon(member, coupon))

                    val host = memberRepository.saveAndFlush(getHost())

                    val space1 = spaceRepository.saveAndFlush(testSpace(host))
                    spaceRepository.saveAndFlush(testSpace(host))

                    val checkIn = LocalDate.now().plusDays(2)
                    val checkOut = LocalDate.now().plusDays(10)

                    val executor = Executors.newFixedThreadPool(2)
                    val barrier = CyclicBarrier(2)

                    for (i in 0..1) {
                        executor.execute {
                            try {
                                barrier.await()
                                val reservationRequest = ReservationRequest(
                                    checkIn = checkIn,
                                    checkOut = checkOut,
                                    reservationPeople = 1,
                                    userCouponId = userCoupon.id!!
                                )
                                reservationService.reserveSpace(member.id!!, space1.id!! + i, reservationRequest)
                            } catch (e: Exception) {
                                exceptionList.add(e)
                            }
                        }
                    }

                    executor.shutdown()
                    executor.awaitTermination(10, TimeUnit.SECONDS)

                    val reservationList = reservationRepository.findAll()

                    reservationList.size shouldBe 1
                    exceptionList.size shouldBe 1
                    exceptionList.forEach { it.shouldBeTypeOf<IllegalStateException>() }
                }
            }
        }
    }

}) {
    companion object {
        val fixture = kotlinFixture()

        fun getMember(): Member {
            return fixture<Member> {
                property(Member::role) { MemberRole.MEMBER }
            }
        }

        fun getHost(): Member {
            return fixture<Member> {
                property(Member::role) { MemberRole.HOST }
            }
        }

        fun getCoupon(): Coupon {
            return fixture<Coupon> {
                property(Coupon::createdAt) { LocalDateTime.now().minusDays(2) }
                property(Coupon::issueStartAt) { LocalDateTime.now().minusDays(1) }
                property(Coupon::issueEndAt) { LocalDateTime.now().plusDays(1) }
                property(Coupon::expirationAt) { LocalDateTime.now().plusDays(10) }
            }
        }

        fun testSpace(host: Member): Space {
            return fixture<Space> {
                property(Space::listingName) { "testSpace" }
                property(Space::price) { 100000 }
                property(Space::defaultPeople) { 2 }
                property(Space::maxPeople) { 4 }
                property(Space::pricePerPerson) { 30000 }
                property(Space::status) { SpaceStatus.ACTIVE }
                property(Space::host) { host }
            }
        }
    }
}