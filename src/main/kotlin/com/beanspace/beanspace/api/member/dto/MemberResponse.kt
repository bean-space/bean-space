package com.beanspace.beanspace.api.member.dto

import com.beanspace.beanspace.domain.member.model.Member

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String
) {
    companion object {
        fun fromEntity(member: Member): MemberResponse {
            return MemberResponse(
                id = member.id!!,
                email = member.email,
                nickname = member.nickname
            )
        }
    }
}