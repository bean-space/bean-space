package com.beanspace.beanspace.domain.coupon.model

import com.appmattus.kotlinfixture.kotlinFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class CouponTest : BehaviorSpec({

    context("Coupon.isCouponStockAvailable()") {
        given("쿠폰의 재고가 남아있는지 확인할 때") {
            `when`("재고가 0 보다 크면") {
                then("true를 반환한다.") {
                    val coupon = fixture<Coupon> { property(Coupon::stock) { 10 } }

                    coupon.isCouponStockAvailable() shouldBe true
                }
            }

            `when`("재고가 0 이하면") {
                then("false를 반환한다.") {
                    val coupon = fixture<Coupon> { property(Coupon::stock) { 0 } }

                    coupon.isCouponStockAvailable() shouldBe false
                }
            }
        }
    }

    context("Coupon.isIssuePeriodValid()") {
        given("쿠폰 발급가능한 기간인지 확인할 때") {
            `when`("현재 시간이 발급시작시간 전이면") {
                then("false를 반환한다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::issueStartAt) { LocalDateTime.now().plusMinutes(2) }
                        property(Coupon::issueEndAt) { LocalDateTime.now().plusMinutes(30) }
                    }

                    coupon.isIssuePeriodValid() shouldBe false
                }
            }

            `when`("현재 시간이 발급종료시간 후이면") {
                then("false를 반환한다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::issueStartAt) { LocalDateTime.now().minusMinutes(30) }
                        property(Coupon::issueEndAt) { LocalDateTime.now().minusMinutes(2) }
                    }

                    coupon.isIssuePeriodValid() shouldBe false

                }
            }

            `when`("현재 시간이 발급시작시간과 발급종료시간 사이면") {
                then("true를 반환한다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::issueStartAt) { LocalDateTime.now().minusMinutes(1) }
                        property(Coupon::issueEndAt) { LocalDateTime.now().plusMinutes(30) }
                    }

                    coupon.isIssuePeriodValid() shouldBe true
                }
            }
        }
    }

    context("Coupon.issueCoupon()") {
        given("총 수량이 50이고 재고가 50인 쿠폰") {
            `when`("1개를 발급하면") {
                then("총 수량은 50이고 재고는 49이다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::totalQuantity) { 50 }
                        property(Coupon::stock) { 50 }
                    }

                    coupon.totalQuantity shouldBe 50
                    coupon.stock shouldBe 50

                    coupon.issueCoupon()

                    coupon.totalQuantity shouldBe 50
                    coupon.stock shouldBe 49
                }
            }
        }
    }

    context("Coupon.calculateDiscountAmount()") {
        given("할인율이 30%이고 최대할인금액이 20000인 쿠폰을") {
            `when`("총 금액이 50000원일때 사용하면") {
                then("할인금액은 15000원 이어야한다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::discountRate) { 30 }
                        property(Coupon::maxDiscount) { 20000 }
                    }

                    coupon.calculateDiscountAmount(50000) shouldBe 15000
                }
            }

            `when`("총 금액이 100000원일때 사용하면") {
                then("할인금액은 20000원 이어야한다.") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::discountRate) { 30 }
                        property(Coupon::maxDiscount) { 20000 }
                    }

                    coupon.calculateDiscountAmount(100000) shouldBe 20000
                }
            }
        }
    }

    context("Coupon.isNotExpired()") {
        given("만료된 쿠폰인지 확인할 때") {
            `when`("현재시간이 만료시간 이전이면") {
                then("True 반환") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::expirationAt) { LocalDateTime.now().plusMinutes(1) }
                    }

                    coupon.isNotExpired() shouldBe true
                }
            }

            `when`("현재시간이 만료시간 후면") {
                then("false 반환") {
                    val coupon = fixture<Coupon> {
                        property(Coupon::expirationAt) { LocalDateTime.now().minusMinutes(1) }
                    }

                    coupon.isNotExpired() shouldBe false
                }
            }
        }

    }
}) {
    companion object {
        val fixture = kotlinFixture()
    }
}
