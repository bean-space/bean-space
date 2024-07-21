package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.UserCoupon

interface CustomUserCouponRepository {
    fun getMemberCouponList(memberId: Long): List<UserCoupon>
}