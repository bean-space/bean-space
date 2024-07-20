package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.space.dto.WishListedSpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val wishListRepository: WishListRepository,
    private val spaceRepository: SpaceRepository
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

    fun getWishListedSpaceList(userPrincipal: UserPrincipal): List<WishListedSpaceResponse> {
        return wishListRepository.findAllByMemberId(userPrincipal.id)
            .map { it.spaceId }
            .let { spaceRepository.findAllByIdInAndStatus(it, SpaceStatus.ACTIVE) }
            .map { WishListedSpaceResponse.fromEntity(it) }
    }
}