package com.beanspace.beanspace.api.member.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateSocialUserInfoRequest(
    @field:NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @field:Pattern(
        regexp = "^010\\d{8}\$",
        message = "휴대폰 번호는 010으로 시작해서 11자로 설정해야합니다."
    )
    val phoneNumber: String
)