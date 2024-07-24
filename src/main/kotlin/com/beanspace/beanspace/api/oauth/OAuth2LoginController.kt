package com.beanspace.beanspace.api.oauth

import com.beanspace.beanspace.api.auth.dto.LoginResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/oauth2/login")
@RestController
class OAuth2LoginController(
    private val oAuth2LoginService: OAuth2LoginService
) {

    @GetMapping("/kakao")
    fun redirectLoginPage(response: HttpServletResponse) {
        val loginPageUrl = oAuth2LoginService.getOAuth2LoginPage()
        response.sendRedirect(loginPageUrl)
    }

    @GetMapping("/callback")
    fun callback(
        @RequestParam code: String
    ): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(oAuth2LoginService.login(code))
    }
}