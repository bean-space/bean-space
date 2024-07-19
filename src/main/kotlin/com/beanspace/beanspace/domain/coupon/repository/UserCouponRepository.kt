package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface UserCouponRepository : JpaRepository<UserCoupon, Long> {
    fun existsByCouponIdAndMemberId(couponId: Long, memberId: Long): Boolean
}