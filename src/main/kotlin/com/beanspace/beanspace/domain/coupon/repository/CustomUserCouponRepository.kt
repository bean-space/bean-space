package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.infra.security.dto.UserPrincipal

interface CustomUserCouponRepository {
    fun getMemberCouponList(userPrincipal: UserPrincipal): List<UserCoupon>
}