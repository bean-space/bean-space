package com.beanspace.beanspace.api.member.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(

    @field:NotBlank(message = "닉네임을 입력해주세요.")
    @field:Size(min = 2, max = 12, message = "닉네임은 2자에서 12자 사이로 설정해야합니다.")
    @field:Pattern(
        regexp = "^[가-힣a-zA-Z0-9]{2,12}+$",
        message = "닉네임은 한글, 영문, 숫자로 설정해야합니다."
    )
    val nickname: String,

    @field:NotBlank(message = "이메일을 입력해주세요.")
    @field:Pattern(
        regexp = "(^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$|^EMPTY$)",
        message = "올바른 이메일 형식을 입력해주세요."
    )
    val email: String,

    val profileImageUrl: String?
)
