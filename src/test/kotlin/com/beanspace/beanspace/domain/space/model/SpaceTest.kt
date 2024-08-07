package com.beanspace.beanspace.domain.space.model

import com.appmattus.kotlinfixture.decorator.optional.AlwaysOptionalStrategy
import com.appmattus.kotlinfixture.decorator.optional.optionalStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.member.model.Member
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SpaceTest : BehaviorSpec({

    context("Space.hasPermission()") {
        given("host의 id가 1인 공간에 대해 권한이 있는지 확인 할 때") {

            val space = fixture<Space> {
                property(Space::host) {
                    fixture<Member> { property(Member::id) { 1L } }
                }
            }

            `when`("접근하는 사용자의 아이디가 1이면") {
                then("true를 반환한다.") {
                    space.hasPermission(1) shouldBe true
                }
            }

            `when`("접근하는 사용자의 아이디가 1이 아니면") {
                then("false를 반환한다.") {
                    space.hasPermission(2) shouldBe false
                }
            }
        }
    }

    context("Space.delete()") {
        given("아직 삭제되지 않은 공간을") {
            `when`("삭제하면") {
                then("isDeleted == true, deletedAt != null이다") {
                    val space = fixture<Space>()

                    space.isDeleted shouldBe false
                    space.deletedAt shouldBe null

                    space.delete()

                    space.isDeleted shouldBe true
                    space.deletedAt shouldNotBe null
                }
            }
        }
    }

    context("Space.calculateTotalCost()") {
        given("기본 인원: 2명, 기본가격: 30000원, 인당추가요금: 10000원인 공간에") {

            val space = fixture<Space> {
                property(Space::defaultPeople) { 2 }
                property(Space::price) { 30000 }
                property(Space::pricePerPerson) { 10000 }
            }

            `when`("2명이 3일동안 예약하면") {
                then("총 요금은 30000 x 3 = 90000원 이다.") {
                    space.calculateTotalCost(2, 3) shouldBe 90000
                }
            }

            `when`("3명이 3일동안 예약하면") {
                then("총 요금은 (30000 + 10000) x 3 = 120000원 이다.") {
                    space.calculateTotalCost(3, 3) shouldBe 120000
                }
            }

            `when`("1명이 3일동안 예약하면") {
                then("총 요금은 30000 x 3 = 90000원 이다.") {
                    space.calculateTotalCost(1, 3) shouldBe 90000
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

