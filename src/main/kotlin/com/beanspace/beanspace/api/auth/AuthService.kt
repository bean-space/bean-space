package com.beanspace.beanspace.api.auth

import com.beanspace.beanspace.api.auth.dto.AuthResponse
import com.beanspace.beanspace.api.auth.dto.LoginRequest
import com.beanspace.beanspace.api.auth.dto.SignUpRequest
import com.beanspace.beanspace.domain.exception.AuthenticationException
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.infra.security.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
) {
    @Transactional
    fun signUp(request: SignUpRequest): AuthResponse {

        if (request.password != request.passwordConfirmation) {
            throw IllegalArgumentException("비밀번호가 서로 일치하지 않습니다.")
        }

        if (memberRepository.existsByEmail(request.email))
            throw IllegalArgumentException("이미 존재하는 email 입니다.")

        return request.toEntity(passwordEncoder)
            .also { memberRepository.save(it) }
            .let { AuthResponse.from(it) }
    }

    fun logIn(request: LoginRequest): AuthResponse {

        val member = memberRepository.findByEmail(request.email)
            ?: throw AuthenticationException("이메일 또는 비밀번호를 확인해주세요.")

        if (member.email != request.email ||
            !passwordEncoder.matches(request.password, member.password)
        )
            throw AuthenticationException("이메일 또는 비밀번호를 확인해주세요.")

        val token = jwtPlugin.generateAccessToken(
            subject = member.id.toString(),
            role = MemberRole.MEMBER.toString()
        )

        return AuthResponse.from(member, token)

    }
}