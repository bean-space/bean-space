package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.member.dto.MemberResponse
import com.beanspace.beanspace.api.member.dto.ProfileUpdateRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
        request: HttpServletRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody profile: ProfileUpdateRequest
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.updateProfile(profile, principal.id))
    }

    @GetMapping("/profile")
    fun getMyProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity
            .ok(memberService.getMyProfile(principal))
    }

    @GetMapping("/wishlist")
    fun getWishListedSpaceList(): ResponseEntity<List<SpaceResponse>> {
        return ResponseEntity
            .ok(memberService.getWishListedSpaceList(/* 인증정보 */))
    }
}