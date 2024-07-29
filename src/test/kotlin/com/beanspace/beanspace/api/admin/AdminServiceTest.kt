package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.UpdateSpaceStatus
import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

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
        given("할인율이") {
            `when`("0~100 범위 내에 없으면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { discountRate } returns 150
                    }
                    shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }

                }
            }
        }

        given("쿠폰 발급일이") {
            `when`("쿠폰 생성일 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { discountRate } returns 15
                        every { issueStartAt } returns LocalDateTime.of(2024, 7, 10, 0, 0, 0, 0)
                    }
                    shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                }
            }
        }

        given("쿠폰 발급 마감일이") {
            `when`("쿠폰 발급일 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { discountRate } returns 15
                        every { issueStartAt } returns LocalDateTime.of(2024, 7, 23, 0, 0, 0, 0)
                        every { issueEndAt } returns LocalDateTime.of(2024, 7, 20, 0, 0, 0, 0)
                    }

                    shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                }
            }
        }

        given("쿠폰 만료일이") {
            `when`("쿠폰 발급 마감일 이전이면") {
                then("IllegalArgumentException이 발생한다") {
                    val request = mockk<CouponRequest> {
                        every { discountRate } returns 15
                        every { issueStartAt } returns LocalDateTime.of(2024, 7, 23, 0, 0, 0, 0)
                        every { issueEndAt } returns LocalDateTime.of(2024, 7, 24, 0, 0, 0, 0)
                        every { expirationAt } returns LocalDateTime.of(2024, 7, 19, 0, 0, 0, 0)
                    }

                    shouldThrow<IllegalArgumentException> { adminService.createCoupon(request) }
                }
            }
        }
    }
}) {
    companion object {
        val defaultPageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
    }
}