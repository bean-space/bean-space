package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.coupon.dto.UserCouponResponse
import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.api.space.dto.WishListedSpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun requestRoleChange(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<Member> {
        return ResponseEntity
            .ok(memberService.requestRoleChange(principal))
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