package com.beanspace.beanspace.api.auth

import com.beanspace.beanspace.api.auth.dto.SignUpRequest
import com.beanspace.beanspace.api.auth.dto.SignUpResponse
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {

        if (request.password != request.passwordConfirmation) {
            throw IllegalArgumentException("비밀번호가 서로 일치하지 않습니다.")
        }

        if (memberRepository.existsByEmail(request.email))
            throw IllegalArgumentException("이미 존재하는 email 입니다.")

        return request.toEntity(passwordEncoder)
            .also { memberRepository.save(it) }
            .let { SignUpResponse.from(it) }
    }
}