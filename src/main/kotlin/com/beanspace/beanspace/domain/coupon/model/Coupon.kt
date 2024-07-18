package com.beanspace.beanspace.domain.coupon.model

import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Coupon(
    @Column
    var name: String,

    @Column
    var discountRate: Int,

    @Column
    var maxDiscount: Int,

    @Column
    var issueStartAt: LocalDateTime,

    @Column
    var issueEndAt: LocalDateTime,

    @Column
    var expirationAt: LocalDateTime,

    @Column
    var totalQuantity: Int,

    @Column
    var stock: Int,

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    fun updateCoupon(request: CouponRequest) {
        name = request.name
        discountRate = request.discountRate
        maxDiscount = request.maxDiscount
        issueStartAt = request.issueStartAt
        issueEndAt = request.issueEndAt
        expirationAt = request.expirationAt
        totalQuantity = request.totalQuantity
    }
}
