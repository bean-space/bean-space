package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.member.dto.MemberResponse
import com.beanspace.beanspace.api.member.dto.ProfileUpdateRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun updateProfile(profile: ProfileUpdateRequest, memberId: Long): MemberResponse {
        val member = memberRepository.findByIdOrNull(memberId) ?: throw ModelNotFoundException("Member", memberId)

        if (profile.password != profile.confirmPassword) throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")

        val password = passwordEncoder.encode(profile.password)

        member.updateProfile(profile, password)

        return MemberResponse.fromEntity(member)
    }

    fun getMyProfile(principal: UserPrincipal): MemberResponse {
        val member = memberRepository.findByIdOrNull(principal.id)
            ?: throw ModelNotFoundException("Member", principal.id)

        return MemberResponse.fromEntity(member)
    }

    fun getWishListedSpaceList(/* 인증 정보 */): List<SpaceResponse> {
        // 유저가 찜한 공간 리스트 조회하기
        TODO()
    }

}