package com.beanspace.beanspace.api.oauth.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoLoginUserInfoResponse(
    val id: String,
    val properties: KakaoUserPropertiesResponse
) {
    fun toEntity(): Member {
        return Member(
            provider = "KAKAO",
            providerId = id,
            nickname = properties.nickname,
            email = null.toString(),
            phoneNumber = null.toString(),
            password = null.toString(),
            role = MemberRole.MEMBER,
            profileImageUrl = null.toString()
        )
    }
}
