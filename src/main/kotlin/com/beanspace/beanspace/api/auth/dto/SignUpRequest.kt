package com.beanspace.beanspace.api.auth.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import org.springframework.security.crypto.password.PasswordEncoder

data class SignUpRequest(
    val email: String,
    val password: String,
    val passwordConfirmation: String,
    val nickname: String,
    val phoneNumber: String,
    val role: MemberRole = MemberRole.MEMBER
)

fun SignUpRequest.toEntity(
    passwordEncoder: PasswordEncoder
): Member {
    return Member(
        email = this.email,
        password = passwordEncoder.encode(this.password),
        nickname = this.nickname,
        phoneNumber = this.phoneNumber,
        role = this.role
    )
}