package com.beanspace.beanspace.api.auth

import com.beanspace.beanspace.api.auth.dto.AuthResponse
import com.beanspace.beanspace.api.auth.dto.LoginRequest
import com.beanspace.beanspace.api.auth.dto.SignUpRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.signUp(request))
    }

    @PostMapping("/login")
    fun logIn(
        @RequestBody request: LoginRequest
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.logIn(request))
    }
}
