package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.RequestAddSpaceResponse
import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(private val adminService: AdminService) {

    @GetMapping("/spaces")
    fun getRequestAddSpace(): ResponseEntity<List<RequestAddSpaceResponse>> {
        return ResponseEntity.ok(adminService.getRequestAddSpace())
    }

    @PatchMapping("/spaces/{spaceId}")
    fun modifySpaceStatus(@PathVariable spaceId: Long): ResponseEntity<Unit> {
        return ResponseEntity.ok(adminService.modifySpaceStatus(spaceId))
    }

    @GetMapping("/members")
    fun getMemberList(): ResponseEntity<Unit> {
        return ResponseEntity.ok(adminService.getMemberList())
    }

    @GetMapping("/coupons")
    fun getCouponList(): ResponseEntity<List<CouponResponse>> {
        return ResponseEntity.ok(adminService.getCouponList())
    }

    @PostMapping("/coupons")
    fun createCoupon(): ResponseEntity<CouponResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCoupon())
    }

    @PutMapping("/coupons/{couponId}")
    fun updateCoupon(@PathVariable couponId: Long): ResponseEntity<CouponResponse> {
        return ResponseEntity.ok(adminService.updateCoupon(couponId))
    }

    @DeleteMapping("/coupons/{couponId}")
    fun deleteCoupon(@PathVariable couponId: Long): ResponseEntity<Unit> {

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}