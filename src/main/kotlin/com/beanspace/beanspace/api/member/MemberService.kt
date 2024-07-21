package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.coupon.dto.UserCouponResponse
import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val userCouponRepository: UserCouponRepository,
) {

    @Transactional
    fun updateProfile(principal: UserPrincipal, request: UpdateProfileRequest): MemberProfileResponse {

        return memberRepository.findByIdOrNull(principal.id)
            ?.also { it.updateProfile(nickname = request.nickname, email = request.email) }
            ?.let { MemberProfileResponse.fromEntity(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }

    fun getProfile(principal: UserPrincipal): MemberProfileResponse {

        return memberRepository.findByIdOrNull(principal.id)
            ?.let { MemberProfileResponse.fromEntity(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }

    fun getWishListedSpaceList(/* 인증 정보 */): List<SpaceResponse> {
        // 유저가 찜한 공간 리스트 조회하기
        TODO()
    }

    fun getCouponList(userPrincipal: UserPrincipal): List<UserCouponResponse> {
        return userCouponRepository.getMemberCouponList(userPrincipal.id)
            .map { UserCouponResponse.from(it) }
    }
}