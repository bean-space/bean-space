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
    val name: String,

    @Column
    val discountRate: Int,

    @Column
    val maxDiscount: Int,

    @Column
    val issueStartAt: LocalDateTime,

    @Column
    val issueEndAt: LocalDateTime,

    @Column
    val expirationAt: LocalDateTime,

    @Column
    val totalQuantity: Int,

    @Column
    val stock: Int,

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
