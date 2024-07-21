package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
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

    @Transactional
    fun requestRoleChange(memberId: Long, requestedRole: MemberRole): Member {

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw IllegalArgumentException("유효하지 않는 맴버 ID입니다.")

        if (member.role == requestedRole) {
            throw IllegalArgumentException("이미 요청한 역할을 갖고 있습니다.")
        }

        member.role = requestedRole
        return memberRepository.save(member)

    }

    fun getWishListedSpaceList(/* 인증 정보 */): List<SpaceResponse> {
        // 유저가 찜한 공간 리스트 조회하기
        TODO()
    }

}