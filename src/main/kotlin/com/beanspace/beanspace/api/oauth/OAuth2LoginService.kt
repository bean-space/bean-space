package com.beanspace.beanspace.api.oauth

import com.beanspace.beanspace.api.auth.AuthService
import com.beanspace.beanspace.api.auth.dto.LoginResponse
import com.beanspace.beanspace.infra.security.jwt.JwtPlugin
import org.springframework.stereotype.Service

@Service
class OAuth2LoginService(
    private val kakaoOAuth2Client: KakaoOAuth2Client,
    private val authService: AuthService,
    private val jwtPlugin: JwtPlugin
) {
    fun getOAuth2LoginPage(): String {
        return kakaoOAuth2Client.getOAuth2LoginPage()
    }

    fun login(code: String): LoginResponse {
        return kakaoOAuth2Client.getAccessToken(code)
            .let { kakaoOAuth2Client.retrieveUserInfo(it) }
            .let { authService.registerIfAbsent(it) }
            .let { jwtPlugin.generateAccessToken(it.id.toString(), it.role.toString()) }
            .let { LoginResponse(it) }
    }
}