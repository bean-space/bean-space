package com.beanspace.beanspace.api.coupon.dto

import com.beanspace.beanspace.domain.coupon.model.Coupon
import java.time.LocalDateTime

data class CouponResponse(
    val id: Long,
    val name: String,
    val discountRate: Int,
    val maxDiscount: Int,
    val issueStartAt: LocalDateTime,
    val issueEndAt: LocalDateTime,
    val expirationAt: LocalDateTime,
    val totalQuantity: Int
) {
    companion object {
        fun from(coupon: Coupon): CouponResponse {
            return CouponResponse(
                coupon.id!!,
                coupon.name,
                coupon.discountRate,
                coupon.maxDiscount,
                coupon.issueStartAt,
                coupon.issueEndAt,
                coupon.expirationAt,
                coupon.totalQuantity
            )
        }
    }
}
