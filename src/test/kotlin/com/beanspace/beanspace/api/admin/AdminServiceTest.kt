package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.UpdateSpaceStatus
import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import kotlin.random.Random

class AdminServiceTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk(relaxed = true)
    val couponRepository: CouponRepository = mockk(relaxed = true)

    val adminService = AdminService(
        spaceRepository = spaceRepository,
        couponRepository = couponRepository
    )

    afterContainer { clearAllMocks() }

    context("AdminService.getRequestAddSpace()") {
        given("Space의 Status로") {
            `when`("존재하지 않는 SpaceStatus를 조회하면") {
                then("IllegalArgumentException이 발생한다") {

                    shouldThrow<IllegalArgumentException> {
                        adminService.getRequestAddSpace(defaultPageable, "ready")
                    }
                }
            }
        }
    }

    context("AdminService.updateSpaceStatus()") {
        given("Space의 Status가") {
            `when`("PENDING 상태가 아닌 Space의 Status를 변경하면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = UpdateSpaceStatus(
                        status = "active"
                    )

                    val activeSpace = mockk<Space> {
                        every { id } returns 1L
                        every { status } returns SpaceStatus.ACTIVE
                        every { isProceeded() } returns false
                    }

                    val rejectedSpace = mockk<Space> {
                        every { id } returns 2L
                        every { status } returns SpaceStatus.REJECTED
                        every { isProceeded() } returns false
                    }

                    every { spaceRepository.findByIdOrNull(1L) } returns activeSpace
                    every { spaceRepository.findByIdOrNull(2L) } returns rejectedSpace

                    shouldThrow<IllegalArgumentException> { adminService.updateSpaceStatus(1L, request) }
                    shouldThrow<IllegalArgumentException> { adminService.updateSpaceStatus(2L, request) }
                }
            }

            `when`("PENDING 상태인 Space의 Status를 존재하지 않는 Status로 변경하면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = UpdateSpaceStatus(
                        status = "active"
                    )

                    val pendingSpace = mockk<Space> {
                        every { id } returns 1L
                        every { status } returns SpaceStatus.PENDING
                        every { isProceeded() } returns true
                    }

                    every { spaceRepository.findByIdOrNull(1L) } returns pendingSpace

                    shouldThrow<IllegalArgumentException> { adminService.updateSpaceStatus(1L, request) }
                }
            }
        }
    }

    context("AdminService.createCoupon()") {
        given("쿠폰 이름의") {
            `when`("길이가 1~30 이 아니라면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns ""
                    }
                    val request2 = mockk<CouponRequest> {
                        every { name } returns "쿠폰이름쿠폰이름쿠폰쿠폰이름쿠폰이름쿠폰쿠폰이름쿠폰이름쿠폰쿠폰"
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "쿠폰 이름은 1 ~ 30까지 입력 가능합니다."

                    val exception2 = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request2) }
                    exception2.message shouldBe "쿠폰 이름은 1 ~ 30까지 입력 가능합니다."
                }
            }
        }

        given("할인율이") {
            `when`("0~100 범위 내에 없으면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns "테스트 쿠폰"
                        every { discountRate } returns 150
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "할인율은 1 ~ 100까지 입력 가능합니다"
                }
            }
        }

        given("최대 할인 금액이") {
            `when`("양의 정수가 아니라면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns "테스트 쿠폰"
                        every { discountRate } returns 15
                        every { maxDiscount } returns Random.nextInt(Int.MIN_VALUE, 0)
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "최대 할인 금액은 양의 정수여야 합니다."
                }
            }
        }

        given("쿠폰 발급 시작 시간이") {
            `when`("현재 시간 보다 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns "테스트 쿠폰"
                        every { discountRate } returns 15
                        every { maxDiscount } returns 10000
                        every { issueStartAt } returns LocalDateTime.now().minusDays(1)
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "발급 시작 시간을 현재 시간 이후로 설정하세요."
                }
            }
        }


        given("쿠폰 발급 마감 시간이") {
            `when`("쿠폰 발급 시작 시간보다 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns "테스트 쿠폰"
                        every { discountRate } returns 15
                        every { maxDiscount } returns 10000
                        every { issueStartAt } returns LocalDateTime.now().plusDays(2)
                        every { issueEndAt } returns LocalDateTime.now().plusDays(1)
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "발급 마감 시간을 발급 시작 시간 이후로 설정하세요"
                }
            }
        }

        given("쿠폰 만료 시간이") {
            `when`("쿠폰 발급 마감 시간보다 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { name } returns "테스트 쿠폰"
                        every { discountRate } returns 15
                        every { maxDiscount } returns 10000
                        every { issueStartAt } returns LocalDateTime.now().plusDays(2)
                        every { issueEndAt } returns LocalDateTime.now().plusDays(4)
                        every { expirationAt } returns LocalDateTime.now().plusDays(3)
                    }
                    val exception = shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                    exception.message shouldBe "쿠폰 만료 시간은 발급 마감 시간보다 빠를 수 없습니다."
                }
            }
        }
    }
}) {
    companion object {
        val defaultPageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
    }
}