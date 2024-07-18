package com.beanspace.beanspace.api.auth.dto

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)