package com.beanspace.beanspace.api.coupon.dto

import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import java.time.LocalDateTime

data class UserCouponResponse(
    val name: String,
    val discountRate: Int,
    val maxDiscount: Int,
    val expirationAt: LocalDateTime,
    val usedAt: LocalDateTime?,
    val userCouponId: Long
) {
    companion object {
        fun from(userCoupon: UserCoupon): UserCouponResponse {
            return UserCouponResponse(
                name = userCoupon.coupon.name,
                discountRate = userCoupon.coupon.discountRate,
                maxDiscount = userCoupon.coupon.maxDiscount,
                expirationAt = userCoupon.coupon.expirationAt,
                usedAt = userCoupon.usedAt,
                userCouponId = userCoupon.id!!
            )
        }
    }
}