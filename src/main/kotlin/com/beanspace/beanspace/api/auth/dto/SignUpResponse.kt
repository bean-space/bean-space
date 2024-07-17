package com.beanspace.beanspace.api.auth.dto

import com.beanspace.beanspace.domain.member.model.Member

data class SignUpResponse(
    val id: Long,
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(saveUser: Member): SignUpResponse {
            return SignUpResponse(
                saveUser.id ?: throw IllegalStateException("저장된 사용자 정보가 유효하지 않습니다."),
                saveUser.email,
                saveUser.nickname,
            )
        }
    }
}