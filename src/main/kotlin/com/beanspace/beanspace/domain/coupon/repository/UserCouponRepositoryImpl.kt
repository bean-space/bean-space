package com.beanspace.beanspace.domain.coupon.repository

import com.beanspace.beanspace.domain.coupon.model.QCoupon
import com.beanspace.beanspace.domain.coupon.model.QUserCoupon
import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.infra.querydsl.QueryDslConfig
import com.beanspace.beanspace.infra.security.dto.UserPrincipal

class UserCouponRepositoryImpl : CustomUserCouponRepository, QueryDslConfig() {

    val userCoupon = QUserCoupon.userCoupon
    val coupon = QCoupon.coupon

    override fun getMemberCouponList(userPrincipal: UserPrincipal): List<UserCoupon> {
        return queryFactory()
            .selectFrom(userCoupon)
            .leftJoin(userCoupon.coupon, coupon)
            .fetchJoin()
            .where(userCoupon.member.id.eq(userPrincipal.id))
            .orderBy(userCoupon.coupon.expirationAt.asc())
            .fetch()
    }
}