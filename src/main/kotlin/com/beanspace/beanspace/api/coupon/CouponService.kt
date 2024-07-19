package com.beanspace.beanspace.api.coupon

import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val userCouponRepository: UserCouponRepository,
    private val memberRepository: MemberRepository,
    private val couponRepository: CouponRepository
) {

    fun getCouponList(): List<CouponResponse> {
        return couponRepository.findAccessibleCoupons()
            .map { CouponResponse.from(it) }
    }

    @Transactional
    fun issueCoupon(principal: UserPrincipal, couponId: Long) {
        val member = memberRepository.findByIdOrNull(principal.id)
            ?: throw ModelNotFoundException("멤버", principal.id)

        val coupon = couponRepository.findByIdOrNull(couponId)
            ?: throw ModelNotFoundException("쿠폰", couponId)

        check(coupon.isCouponStockAvailable()) { throw IllegalStateException("쿠폰 발급이 마감 되었습니다.") }

        check(coupon.isIssuePeriodValid()) { throw IllegalStateException("쿠폰 발급 가능 시간을 확인해주세요.") }

        coupon.issueCoupon()

        UserCoupon(member, coupon)
            .let { userCouponRepository.save(it) }
    }
}