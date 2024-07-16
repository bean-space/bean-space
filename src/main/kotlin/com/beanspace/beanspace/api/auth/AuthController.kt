package com.beanspace.beanspace.api.auth

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    @PostMapping("/sign-up")
    fun signUp() {
        //TODO
    }
}