package com.beanspace.beanspace.api.auth.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole

data class SignUpRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val phoneNumber: String,
    val role: MemberRole = MemberRole.MEMBER
)

fun SignUpRequest.toEntity(): Member {
    return Member(
        email = this.email,
        password = this.password,
        nickname = this.nickname,
        phoneNumber = this.phoneNumber,
        role = this.role
    )
}