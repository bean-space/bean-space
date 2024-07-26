package com.beanspace.beanspace.api.coupon

import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(
    private val couponService: CouponService
) {

    @GetMapping
    fun getCouponList(): ResponseEntity<List<CouponResponse>> {
        return ResponseEntity.ok(couponService.getCouponList())
    }

    @PostMapping("/{couponId}")
    fun issueCoupon(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable couponId: Long
    ): ResponseEntity<Unit> {
        couponService.issueCoupon(principal, couponId)
        return ResponseEntity.ok().build()
    }
}