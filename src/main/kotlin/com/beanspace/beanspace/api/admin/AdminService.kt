package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.RequestAddSpaceResponse
import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val couponRepository: CouponRepository,
) {
    fun getRequestAddSpace(): List<RequestAddSpaceResponse> {
        TODO()
    }

    fun modifySpaceStatus(spaceId: Long) {
        //TODO
    }

    fun getMemberList() {
        TODO()
    }

    fun getCouponList(): List<CouponResponse> {
        return couponRepository.findAll().map { CouponResponse.from(it) }
    }

    fun createCoupon(): CouponResponse {
        TODO()
    }

    fun updateCoupon(couponId: Long): CouponResponse {
        TODO()
    }

    fun deleteCoupon(couponId: Long) {
        TODO()
    }
}