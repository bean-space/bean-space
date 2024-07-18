package com.beanspace.beanspace.domain.coupon.model

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
    fun update(
        name: String,
        discountRate: Int,
        maxDiscount: Int,
        issueStartAt: LocalDateTime,
        issueEndAt: LocalDateTime,
        expirationAt: LocalDateTime,
        totalQuantity: Int
    ) {
        this.name = name
        this.discountRate = discountRate
        this.maxDiscount = maxDiscount
        this.issueStartAt = issueStartAt
        this.issueEndAt = issueEndAt
        this.expirationAt = expirationAt
        this.totalQuantity = totalQuantity
    }
}
