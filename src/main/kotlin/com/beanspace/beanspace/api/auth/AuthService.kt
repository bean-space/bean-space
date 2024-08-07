package com.beanspace.beanspace.api.auth

import com.beanspace.beanspace.api.auth.dto.LoginRequest
import com.beanspace.beanspace.api.auth.dto.LoginResponse
import com.beanspace.beanspace.api.auth.dto.SignUpRequest
import com.beanspace.beanspace.api.oauth.dto.KakaoLoginUserInfoResponse
import com.beanspace.beanspace.domain.exception.AuthenticationException
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.infra.security.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
) {

    fun signUp(request: SignUpRequest) {

        if (request.password != request.passwordConfirmation) {
            throw IllegalArgumentException("비밀번호가 서로 일치하지 않습니다.")
        }

        if (memberRepository.existsByPhoneNumber(request.phoneNumber))
            throw IllegalArgumentException("이미 존재하는 휴대폰 번호 입니다.")

        request.toEntity(passwordEncoder)
            .also { memberRepository.save(it) }
    }

    fun logIn(request: LoginRequest): LoginResponse {

        val member = memberRepository.findByPhoneNumber(request.phoneNumber)
            ?: throw AuthenticationException("휴대폰 번호 또는 비밀번호를 확인해주세요.")

        if (!passwordEncoder.matches(request.password, member.password))
            throw AuthenticationException("휴대폰 번호 또는 비밀번호를 확인해주세요.")

        val token = jwtPlugin.generateAccessToken(
            subject = member.id.toString(),
            role = member.role.name
        )

        return LoginResponse(token)
    }

    fun registerIfAbsent(userInfo: KakaoLoginUserInfoResponse): Member {
        return memberRepository.findByProviderAndProviderId("KAKAO", userInfo.id)
            ?: memberRepository.save(userInfo.toEntity())
    }
}