package com.beanspace.beanspace.api.auth

import com.beanspace.beanspace.api.auth.dto.SignUpRequest
import com.beanspace.beanspace.api.auth.dto.SignUpResponse
import com.beanspace.beanspace.api.auth.dto.toEntity
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository

) {
    @Transactional
    fun signUp(request: SignUpRequest): SignUpResponse {

        if (memberRepository.existsByEmail(request.email))
            throw IllegalArgumentException()

        return SignUpResponse.from(memberRepository.save(request.toEntity()))
    }
}