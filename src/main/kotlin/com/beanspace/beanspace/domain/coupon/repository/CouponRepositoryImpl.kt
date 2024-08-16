package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.Coupon
import com.beanspace.beanspace.domain.coupon.model.QCoupon
import com.beanspace.beanspace.infra.querydsl.QueryDslConfig
import java.time.LocalDateTime

class CouponRepositoryImpl : CustomCouponRepository, QueryDslConfig() {
    val coupon = QCoupon.coupon

    override fun findAccessibleCoupons(): List<Coupon> {
        val now = LocalDateTime.now()

        // 발급 시작시간이 이 시간보다 이전인 쿠폰만 조회되어야 함
        val startTime = now.plusDays(3).toLocalDate().atStartOfDay()

        // 발급 마감 시간이 이 시간 이후인 쿠폰만 조회되어야 함
        val endTime = now.toLocalDate().atStartOfDay()

        return queryFactory()
            .selectFrom(coupon)
            .where(
                coupon.issueStartAt.lt(startTime),
                coupon.issueEndAt.gt(endTime)
            )
            .fetch()
    }
}