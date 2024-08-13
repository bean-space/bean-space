package com.beanspace.beanspace.api.coupon.dto

import com.beanspace.beanspace.domain.coupon.model.Coupon
import java.time.LocalDateTime

data class CouponRequest(
    val name: String,
    val discountRate: Int,
    val maxDiscount: Int,
    val issueStartAt: LocalDateTime,
    val issueEndAt: LocalDateTime,
    val expirationAt: LocalDateTime,
    val totalQuantity: Int
) {
    fun toEntity(): Coupon {
        return Coupon(
            name = name,
            discountRate = discountRate,
            maxDiscount = maxDiscount,
            issueStartAt = issueStartAt,
            issueEndAt = issueEndAt,
            expirationAt = expirationAt,
            totalQuantity = totalQuantity,
            stock = totalQuantity
        )
    }
}
