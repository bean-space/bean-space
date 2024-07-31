package com.beanspace.beanspace.api.coupon

import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.coupon.model.Coupon
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.infra.querydsl.QueryDslConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@ActiveProfiles("test")
class CouponServiceDBTest @Autowired constructor(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    beforeEach {
        couponRepository.deleteAll()
        userCouponRepository.deleteAll()
        memberRepository.deleteAll()
    }

    val couponService = CouponService(
        userCouponRepository = userCouponRepository,
        memberRepository = memberRepository,
        couponRepository = couponRepository
    )

    context("CouponService.issueCoupon()") {
        given("300명의 유저가") {
            `when`("200개 재고의 쿠폰을 동시에 발급 받을 때") {
                then("발급된 쿠폰은 200개이다") {
                    val couponQuantity = 200
                    val tryCouponIssue = 300
                    var successCnt = 0
                    var exceptionCnt = 0

                    for (i in 1..tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    val coupon = couponRepository.saveAndFlush(testCoupon(couponQuantity))

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong() + 1, coupon.id!!)
                                successCnt++
                            } catch (e: Exception) {
                                exceptionCnt++
                            }
                        }
                    }
                    executor.shutdown()
                    executor.awaitTermination(1, TimeUnit.MINUTES)

                    couponRepository.flush()

                    val couponUserSize = userCouponRepository.findAll().size
                    val stock = couponRepository.findByIdOrNull(1L)?.stock
                    println("userCouponRepository.size: $couponUserSize")
                    println("couponRepository.stock: $stock")

                    successCnt shouldBe couponQuantity
                    exceptionCnt shouldBe tryCouponIssue - couponQuantity
                    couponUserSize shouldBe couponQuantity
                    stock shouldBe 0
                }
            }

            `when`("300개 재고의 쿠폰을 동시에 발급 받을 때") {
                then("발급된 쿠폰은 300개이다") {
                    val couponQuantity = 300
                    val tryCouponIssue = 300
                    var successCnt = 0

                    val coupon = testCoupon(couponQuantity)
                    for (i in 1..tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    couponRepository.saveAndFlush(coupon)

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong() + 1, 1L)
                                successCnt++
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    }
                    executor.shutdown()
                    executor.awaitTermination(1, TimeUnit.MINUTES)

                    val couponUserSize = userCouponRepository.findAll().size
                    val stock = couponRepository.findByIdOrNull(1L)?.stock
                    println("userCouponRepository.size: $couponUserSize")
                    println("couponRepository.stock: $stock")

                    successCnt shouldBe couponQuantity
                    couponUserSize shouldBe tryCouponIssue
                    stock shouldBe 0
                }
            }
        }

        given("250명의 유저가") {
            `when`("200개 재고의 쿠폰을 동시에 발급 하고 마감되면") {
                then("IllegalStateException이 발생한다") {
                    val couponQuantity = 200
                    val tryCouponIssue = 250
                    val exceptionQuantity = tryCouponIssue - couponQuantity
                    var successCnt = 0
                    var exceptionCnt = 0
                    val exceptionList = mutableListOf<Exception>()

                    val coupon = testCoupon(couponQuantity)
                    for (i in 1..tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    couponRepository.saveAndFlush(coupon)

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong() + 1, 1L)
                                successCnt++
                            } catch (e: Exception) {
                                exceptionList.add(e)
                                exceptionCnt++
                            }
                        }
                    }
                    executor.shutdown()
                    executor.awaitTermination(1, TimeUnit.MINUTES)

                    val couponUserSize = userCouponRepository.findAll().size
                    val stock = couponRepository.findByIdOrNull(1L)?.stock
                    println("userCouponRepository.size: $couponUserSize")
                    println("couponRepository.stock: $stock")

                    successCnt shouldBe couponQuantity
                    exceptionCnt shouldBe exceptionQuantity
                    couponUserSize shouldBe couponQuantity
                    stock shouldBe 0

                    exceptionList.size shouldBe exceptionQuantity
                    exceptionList.forEach { it.shouldBeTypeOf<IllegalStateException>() }
                }
            }


            `when`("500개 재고의 쿠폰을 동시에 발급 받을 때") {
                then("발급된 쿠폰은 250개이다") {
                    val couponQuantity = 500
                    val tryCouponIssue = 250
                    var successCnt = 0

                    val coupon = testCoupon(couponQuantity)
                    for (i in 1..tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    couponRepository.saveAndFlush(coupon)

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong() + 1, 1L)
                                successCnt++
                            } catch (e: Exception) {
                                println("Error: ${e.message}")
                            }
                        }
                    }
                    executor.shutdown()
                    executor.awaitTermination(1, TimeUnit.MINUTES)

                    val couponUserSize = userCouponRepository.findAll().size
                    val stock = couponRepository.findByIdOrNull(1L)?.stock

                    successCnt shouldBe tryCouponIssue
                    couponUserSize shouldBe tryCouponIssue
                    stock shouldBe couponQuantity - tryCouponIssue
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

        fun testCoupon(couponQuantity: Int): Coupon {
            return fixture<Coupon> {
                property(Coupon::name) { "test" }
                property(Coupon::discountRate) { 10 }
                property(Coupon::issueStartAt) { LocalDateTime.of(2024, 6, 30, 0, 0, 0) }
                property(Coupon::issueEndAt) { LocalDateTime.of(2026, 6, 30, 0, 0, 0) }
                property(Coupon::expirationAt) { LocalDateTime.of(2026, 7, 30, 0, 0, 0) }
                property(Coupon::createdAt) { LocalDateTime.of(2024, 5, 30, 0, 0, 0) }
                property(Coupon::totalQuantity) { couponQuantity }
                property(Coupon::stock) { couponQuantity }
            }
        }
    }
}
