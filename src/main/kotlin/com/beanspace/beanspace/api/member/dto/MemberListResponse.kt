package com.beanspace.beanspace.api.member.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole

data class MemberListResponse(
    val id: Long,
    val nickname: String,
    val role: MemberRole,
) {
    companion object {
        fun from(member: Member): MemberListResponse {
            return MemberListResponse(
                member.id!!,
                member.nickname,
                member.role,
            )
        }
    }
}
