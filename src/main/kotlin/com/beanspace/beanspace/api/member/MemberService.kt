package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository
) {
    fun getWishListedSpaceList(/* 인증 정보 */): List<SpaceResponse> {
        // 유저가 찜한 공간 리스트 조회하기
        TODO()
    }
}