package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, Long>, CustomCouponRepository {
    fun findAllByOrderByIssueStartAtDesc(): List<Coupon>
}