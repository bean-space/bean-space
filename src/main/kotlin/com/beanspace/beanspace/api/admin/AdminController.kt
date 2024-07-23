package com.beanspace.beanspace.api.admin

import com.beanspace.beanspace.api.admin.dto.RequestAddSpaceResponse
import com.beanspace.beanspace.api.admin.dto.UpdateSpaceStatus
import com.beanspace.beanspace.api.coupon.dto.CouponRequest
import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(private val adminService: AdminService) {

    @GetMapping("/spaces")
    fun getRequestAddSpace(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        @RequestParam("status", defaultValue = "PENDING") status: String
    ): ResponseEntity<Page<RequestAddSpaceResponse>> {
        return ResponseEntity.ok(adminService.getRequestAddSpace(pageable, status))
    }

    @PatchMapping("/spaces/{spaceId}")
    fun updateSpaceStatus(
        @PathVariable spaceId: Long,
        @RequestBody request: UpdateSpaceStatus
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(adminService.updateSpaceStatus(spaceId, request))
    }

    @GetMapping("/coupons")
    fun getCouponList(): ResponseEntity<List<CouponResponse>> {
        return ResponseEntity.ok(adminService.getCouponList())
    }

    @PostMapping("/coupons")
    fun createCoupon(@RequestBody request: CouponRequest): ResponseEntity<CouponResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCoupon(request))
    }

    @PutMapping("/coupons/{couponId}")
    fun updateCoupon(
        @PathVariable couponId: Long,
        @RequestBody request: CouponRequest
    ): ResponseEntity<CouponResponse> {
        return ResponseEntity.ok(adminService.updateCoupon(couponId, request))
    }

    @DeleteMapping("/coupons/{couponId}")
    fun deleteCoupon(@PathVariable couponId: Long): ResponseEntity<Unit> {
        adminService.deleteCoupon(couponId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}