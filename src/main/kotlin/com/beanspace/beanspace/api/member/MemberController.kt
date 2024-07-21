package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.RoleChangeRequest
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
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
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<MemberProfileResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.updateProfile(principal, request))
    }

    @GetMapping("/profile")
    fun getProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<MemberProfileResponse> {
        return ResponseEntity
            .ok(memberService.getProfile(principal))
    }

    @PostMapping("/{memberId}/request-role")
    fun requestRoleChange(
        @PathVariable memberId: Long,
        @RequestBody request: RoleChangeRequest
    ): ResponseEntity<Member> {
        val updatedMember = memberService.requestRoleChange(memberId, request.requestedRole)

        return ResponseEntity.ok(updatedMember)
    }

    @GetMapping("/wishlist")
    fun getWishListedSpaceList(): ResponseEntity<List<SpaceResponse>> {
        return ResponseEntity
            .ok(memberService.getWishListedSpaceList(/* 인증정보 */))
    }
}