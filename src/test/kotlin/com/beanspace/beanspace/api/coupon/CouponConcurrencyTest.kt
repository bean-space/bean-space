package com.beanspace.beanspace.api.coupon

import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.coupon.model.Coupon
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class CouponConcurrencyTest @Autowired constructor(
    private val couponService: CouponService,
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    context("CouponService.issueCoupon()") {
        beforeEach {
            couponRepository.deleteAll()
            userCouponRepository.deleteAll()
            memberRepository.deleteAll()
        }
        given("300명의 유저가") {
            `when`("200개 재고의 쿠폰을 동시에 발급 받을 때") {
                then("발급된 쿠폰은 200개이다") {
                    val couponQuantity = 200
                    val tryCouponIssue = 300
                    var successCnt = 0
                    var exceptionCnt = 0

                    repeat(tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    val coupon = couponRepository.saveAndFlush(getCoupon(couponQuantity))

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong(), coupon.id!!)
                                successCnt++
                            } catch (e: Exception) {
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
                    println("successCnt: $successCnt")
                    println("exceptionCnt: $exceptionCnt")

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

                    repeat(tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    val coupon = couponRepository.saveAndFlush(getCoupon(couponQuantity))

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong(), coupon.id!!)
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
                    println("successCnt: $successCnt")

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

                    repeat(tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    val coupon = couponRepository.saveAndFlush(getCoupon(couponQuantity))

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong(), coupon.id!!)
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
                    println("successCnt: $successCnt")
                    println("exceptionCnt: $exceptionCnt")


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

                    repeat(tryCouponIssue) {
                        memberRepository.saveAndFlush(getMember())
                    }

                    val coupon = couponRepository.saveAndFlush(getCoupon(couponQuantity))

                    val executor = Executors.newFixedThreadPool(tryCouponIssue)
                    val barrier = CyclicBarrier(tryCouponIssue)

                    for (i in 1..tryCouponIssue) {
                        executor.submit {
                            try {
                                barrier.await()
                                couponService.issueCoupon(i.toLong(), coupon.id!!)
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

        fun getCoupon(couponQuantity: Int): Coupon {
            return Coupon(
                name = "test",
                discountRate = 10,
                maxDiscount = 5000,
                issueStartAt = LocalDateTime.of(2024, 6, 30, 0, 0, 0),
                issueEndAt = LocalDateTime.of(2026, 6, 30, 0, 0, 0),
                expirationAt = LocalDateTime.of(2026, 7, 30, 0, 0, 0),
                createdAt = LocalDateTime.of(2024, 5, 30, 0, 0, 0),
                totalQuantity = couponQuantity,
                stock = couponQuantity
            )
        }
    }
}