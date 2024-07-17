package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping("/profile")
    fun updateProfile() {
        //TODO 생성자 부분에 추가 HttpServletRequest, UserPrincipal, UpdateProfileDto,
    }

    @GetMapping("/profile")
    fun getMemberProfile() {
        //TODO 프로필 조회 로직
    }

    @GetMapping("/wishlist")
    fun getWishListedSpaceList(): ResponseEntity<List<SpaceResponse>> {
        return ResponseEntity
            .ok(memberService.getWishListedSpaceList(/* 인증정보 */))
    }
}