package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.RequestAddSpaceResponse
import com.beanspace.beanspace.api.admin.dto.UpdateSpaceStatus
import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.api.member.dto.MemberListResponse
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService(
    private val spaceRepository: SpaceRepository,
    private val memberRepository: MemberRepository,
    private val couponRepository: CouponRepository
) {
    fun getRequestAddSpace(pageable: Pageable, status: String): Page<RequestAddSpaceResponse> {
        // 권한 확인 추가
        val spaceStatus = when (status) {
            "PENDING" -> SpaceStatus.PENDING
            "REJECTED" -> SpaceStatus.REJECTED
            "ACTIVE" -> SpaceStatus.ACTIVE
            else -> throw IllegalArgumentException("해당하는 상태의 요청이 없습니다.")
        }
        return spaceRepository.findByStatus(pageable, spaceStatus).map { RequestAddSpaceResponse.from(it) }
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

    fun getMemberList(): List<MemberListResponse> {
        return memberRepository.findAll().map { MemberListResponse.from(it) }
    }

    fun getCouponList(): List<CouponResponse> {
        // 권한 확인 추가
        return couponRepository.findAll().map { CouponResponse.from(it) }
    }

    @Transactional
    fun createCoupon(request: CouponRequest): CouponResponse {
        requestValidation(request)

        return CouponResponse.from(couponRepository.save(request.toEntity()))
    }

    @Transactional
    fun updateCoupon(couponId: Long, request: CouponRequest): CouponResponse {
        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException("Coupon", couponId)

        requestValidation(request)

        coupon.updateCoupon(request)

        return CouponResponse.from(coupon)
    }

    @Transactional
    fun deleteCoupon(couponId: Long) {
        val coupon = couponRepository.findByIdOrNull(couponId) ?: throw ModelNotFoundException("Coupon", couponId)

        couponRepository.delete(coupon)
    }

    private fun requestValidation(request: CouponRequest) {
        validDiscountRate(request.discountRate)
        if (dateCompare(LocalDateTime.now(), request.issueStartAt))
            throw IllegalArgumentException("발급 시작일이 오늘보다 빠를 수 없습니다.")
        if (dateCompare(request.issueStartAt, request.issueEndAt))
            throw IllegalArgumentException("발급 마감일이 시작일보다 빠를 수 없습니다.")
        if (dateCompare(request.issueEndAt, request.expirationAt))
            throw IllegalArgumentException("쿠폰 만료일이 발급 마감일보다 빠를 수 없습니다.")
    }

    private fun validDiscountRate(discountRate: Int) {
        val rate = discountRate.toString()
        if ("^[\\s0-9]{1,3}$".toRegex().matches(rate)) {
            if (rate[0] == '0' || rate.length == 3 && rate[0] != '1')
                throw IllegalArgumentException("할인율은 1 ~ 100까지 입력 가능합니다")
        } else {
            throw IllegalArgumentException("할인율은 1 ~ 100까지 입력 가능합니다")
        }
    }

    private fun dateCompare(earlyDate: LocalDateTime, lateDate: LocalDateTime): Boolean {
        return earlyDate.isAfter(lateDate)
    }
}