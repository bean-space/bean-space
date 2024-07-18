package com.beanspace.beanspace.api.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)