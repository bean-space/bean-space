package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.Coupon

interface CustomCouponRepository {
    fun findAccessibleCoupons(): List<Coupon>
}