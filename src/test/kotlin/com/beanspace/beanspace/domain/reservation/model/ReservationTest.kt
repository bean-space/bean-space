package com.beanspace.beanspace.domain.reservation.model

import com.appmattus.kotlinfixture.decorator.optional.AlwaysOptionalStrategy
import com.appmattus.kotlinfixture.decorator.optional.optionalStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.member.model.Member
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import java.time.LocalDate
import java.time.LocalDateTime

class ReservationTest : BehaviorSpec({

    beforeTest { clearAllMocks() }

    context("Reservation.validateOwner()") {
        given("예약자의 id가 1인 예약에 대해") {
            val reservation = fixture<Reservation> {
                property(Reservation::member) {
                    fixture<Member> { property(Member::id) { 1L } }
                }
            }

            `when`("id가 1인 사용자면") {
                then("true를 반환한다.") {
                    reservation.validateOwner(1) shouldBe true
                }
            }
            `when`("id가 1인 아닌 사용자면") {
                then("false를 반환한다.") {
                    reservation.validateOwner(2) shouldBe false
                }
            }
        }
    }

    context("Reservation.isBeforeCancellationDeadline()") {
        given("체크인 날짜가 2024-07-05인 예약을 취소하려 할때") {
            val reservation = fixture<Reservation> {
                property(Reservation::checkIn) { LocalDate.of(2024, 7, 5) }
            }

            `when`("현재 날짜가 체크인 날짜 2일 전인 2024-07-03이면") {
                then("true를 반환한다.") {
                    reservation.isBeforeCancellationDeadline(LocalDate.of(2024, 7, 3)) shouldBe true
                }
            }

            `when`("현재 날짜가 체크인 날짜 1일 전인 2024-07-04이면") {
                then("false를 반환한다.") {
                    reservation.isBeforeCancellationDeadline(LocalDate.of(2024, 7, 4)) shouldBe false
                }
            }
        }
    }

    context("Reservation.isReviewAllowed") {
        given("채크아웃 날짜가 2024-07-05인 예약에 대한 리뷰를 남기려고 할때") {
            val reservation = fixture<Reservation> {
                property(Reservation::checkOut) { LocalDate.of(2024, 7, 5) }
            }

            `when`("현재 날짜가 2024-07-04이면") {
                then("false를 반환한다.") {
                    reservation.isReviewAllowed(
                        LocalDateTime.of(2024, 7, 4, 12, 0, 0)
                    ) shouldBe false
                }
            }

            `when`("현재 시간이 체크아웃 당일 12:00 이전이면") {
                then("false를 반환한다.") {
                    reservation.isReviewAllowed(
                        LocalDateTime.of(2024, 7, 5, 11, 55, 0)
                    ) shouldBe false

                    reservation.isReviewAllowed(
                        LocalDateTime.of(2024, 7, 5, 12, 0, 0)
                    ) shouldBe false
                }
            }

            `when`("현재 시간이 체크아웃 당일 12:00 후면") {
                then("true를 반환한다.") {
                    reservation.isReviewAllowed(
                        LocalDateTime.of(2024, 7, 5, 12, 0, 1)
                    ) shouldBe true
                }
            }
        }
    }
}) {
    companion object {
        val fixture = kotlinFixture {
            optionalStrategy(AlwaysOptionalStrategy) {}
        }
    }
}