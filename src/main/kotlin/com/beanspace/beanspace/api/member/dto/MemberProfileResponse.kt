package com.beanspace.beanspace.api.member.dto

import com.beanspace.beanspace.domain.member.model.Member

data class MemberProfileResponse(
    val id: Long,
    val email: String,
    val role: String,
    val nickname: String,
    val profileImageUrl: String?
) {
    companion object {
        fun fromEntity(member: Member): MemberProfileResponse {
            return MemberProfileResponse(
                id = member.id!!,
                email = member.email,
                role = member.role.name,
                nickname = member.nickname,
                profileImageUrl = member.profileImageUrl
            )
        }
    }
}