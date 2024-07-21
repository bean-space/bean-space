package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.auth.dto.LoginResponse
import com.beanspace.beanspace.api.coupon.dto.UserCouponResponse
import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.WishListedSpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping("/profile")
    fun updateProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<MemberProfileResponse> {
        return ResponseEntity
            .ok()
            .body(memberService.updateProfile(principal, request))
    }

    @GetMapping("/profile")
    fun getProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<MemberProfileResponse> {
        return ResponseEntity
            .ok(memberService.getProfile(principal))
    }

    @PatchMapping("/request-host")
    fun updateRoleToHost(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .ok(memberService.updateRoleToHost(principal))
    }

    @GetMapping("/wishlist")
    fun getWishListedSpaceList(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<List<WishListedSpaceResponse>> {
        return ResponseEntity
            .ok(memberService.getWishListedSpaceList(userPrincipal))
    }

    @GetMapping("/couponList")
    fun getMemberCouponList(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<List<UserCouponResponse>> {
        return ResponseEntity
            .ok(memberService.getCouponList(userPrincipal))
    }
}