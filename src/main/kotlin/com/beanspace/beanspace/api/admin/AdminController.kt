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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAnyRole('ADMIN')")
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

    @DeleteMapping("/old-keywords")
    fun deleteOldKeywords(): ResponseEntity<Unit> {
        adminService.deleteOldKeywords()
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}