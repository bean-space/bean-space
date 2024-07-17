package com.beanspace.beanspace.api.auth.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.password.PasswordEncoder

data class SignUpRequest(

    @field:NotBlank(message = "이메일을 입력해주세요.")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "올바른 이메일 형식을 입력해주세요."
    )
    val email: String = "",

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이로 설정해야합니다.")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
        message = "비밀번호는 영어, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String = "",

    val passwordConfirmation: String,

    @field:NotBlank(message = "닉네임을 입력해주세요.")
    @field:Size(min = 2, max = 12, message = "닉네임은 2자에서 12자 사이로 설정해야합니다.")
    @field:Pattern(
        regexp = "^[가-힣a-zA-Z0-9]{2,12}+$",
        message = "닉네임은 한글, 영문, 숫자로 설정해야합니다."
    )
    val nickname: String = "",

    @field:NotBlank(message = "전화번호를 입력해주세요.")
    @field:Pattern(
        regexp = "^[0-9]{11}",
        message = "전화번호는 숫자 11자로 설정해야합니다."
    )
    val phoneNumber: String = "",

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