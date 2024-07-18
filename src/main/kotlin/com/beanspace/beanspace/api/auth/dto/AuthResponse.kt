package com.beanspace.beanspace.api.auth.dto

import com.beanspace.beanspace.domain.member.model.Member

data class AuthResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val phoneNumber: String,
    val role: String,
) {
    var token: String? = null

    companion object {
        fun from(saveMember: Member): AuthResponse {
            return AuthResponse(
                saveMember.id ?: throw IllegalStateException("저장된 사용자 정보가 유효하지 않습니다."),
                saveMember.email,
                saveMember.nickname,
                saveMember.phoneNumber,
                saveMember.role.name
            )
        }

        fun from(saveMember: Member, token: String): AuthResponse {
            val authResponse = AuthResponse(
                saveMember.id ?: throw IllegalStateException("저장된 사용자 정보가 유효하지 않습니다."),
                saveMember.email,
                saveMember.nickname,
                saveMember.phoneNumber,
                saveMember.role.name
            )
            authResponse.token = token

            return authResponse
        }
    }
}