package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.RequestAddSpaceResponse
import com.beanspace.beanspace.api.admin.dto.UpdateSpaceStatus
import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService(
    private val spaceRepository: SpaceRepository,
    private val couponRepository: CouponRepository
) {
    fun getRequestAddSpace(pageable: Pageable, status: String): Page<RequestAddSpaceResponse> {
        val spaceStatus = when (status) {
            "PENDING" -> SpaceStatus.PENDING
            "REJECTED" -> SpaceStatus.REJECTED
            "ACTIVE" -> SpaceStatus.ACTIVE
            else -> throw IllegalArgumentException("해당하는 상태의 요청이 없습니다.")
        }

        val (contents, totalCount) = spaceRepository.findByStatus(pageable, spaceStatus)

        if (contents.isEmpty() || totalCount == 0L) {
            return Page.empty()
        }

        val response = contents.map { RequestAddSpaceResponse.from(it.key!!, it.value) }

        return PageImpl(response, pageable, totalCount)
    }

    fun updateSpaceStatus(spaceId: Long, request: UpdateSpaceStatus) {
        val space = spaceRepository.findByIdOrNull(spaceId) ?: throw ModelNotFoundException("Space", spaceId)

        if (space.isProceeded())
            throw IllegalArgumentException("이미 변경한 요청입니다.")

        when (request.status) {
            SpaceStatus.ACTIVE.name -> {
                space.active()
                spaceRepository.save(space)
            }

            SpaceStatus.REJECTED.name -> {
                space.reject()
                spaceRepository.save(space)
            }

            else -> {
                throw IllegalArgumentException("유효하지 않은 요청 입니다.")
            }
        }
    }

    fun getCouponList(): List<CouponResponse> {
        return couponRepository.findAllByOrderByIssueStartAtDesc().map { CouponResponse.from(it) }
    }

    @Transactional
    fun createCoupon(request: CouponRequest): CouponResponse {
        validateRequest(request)

        return CouponResponse.from(couponRepository.save(request.toEntity()))
    }

    @Transactional
    fun updateCoupon(couponId: Long, request: CouponRequest): CouponResponse {
        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException("Coupon", couponId)

        validateRequest(request)

        coupon.update(
            name = request.name,
            discountRate = request.discountRate,
            maxDiscount = request.maxDiscount,
            issueStartAt = request.issueStartAt,
            issueEndAt = request.issueEndAt,
            expirationAt = request.expirationAt,
            totalQuantity = request.totalQuantity
        )

        return CouponResponse.from(coupon)
    }

    @Transactional
    fun deleteCoupon(couponId: Long) {
        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException("Coupon", couponId)

        couponRepository.delete(coupon)
    }

    private fun validateRequest(request: CouponRequest) {

        check(isValidCouponName(request.name)) {
            throw IllegalArgumentException("쿠폰 이름은 1 ~ 30까지 입력 가능합니다.")
        }

        check(isValidDiscountRate(request.discountRate)) {
            throw IllegalArgumentException("할인율은 1 ~ 100까지 입력 가능합니다")
        }

        check(isValidMaxDiscount(request.maxDiscount)) {
            throw IllegalArgumentException("최대 할인 금액은 양의 정수여야 합니다.")
        }

        check(isValidDate(LocalDateTime.now(), request.issueStartAt)) {
            throw IllegalArgumentException("발급 시작 시간을 현재 시간 이후로 설정하세요.")
        }

        check(isValidDate(request.issueStartAt, request.issueEndAt)) {
            throw IllegalArgumentException("발급 마감 시간을 발급 시작 시간 이후로 설정하세요")
        }

        check(isValidDate(request.issueEndAt, request.expirationAt)) {
            throw IllegalArgumentException("쿠폰 만료 시간은 발급 마감 시간보다 빠를 수 없습니다.")
        }
    }

    private fun isValidCouponName(name: String): Boolean {
        return Regex("^.{1,30}$") matches name
    }

    private fun isValidDiscountRate(discountRate: Int): Boolean {
        return (discountRate in 1..100)
    }

    private fun isValidMaxDiscount(maxDiscount: Int): Boolean {
        return (maxDiscount > 0)
    }

    private fun isValidDate(earlyDate: LocalDateTime, lateDate: LocalDateTime): Boolean {
        return earlyDate.isBefore(lateDate)
    }
}