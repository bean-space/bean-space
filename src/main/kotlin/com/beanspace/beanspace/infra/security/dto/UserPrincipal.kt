package com.beanspace.beanspace.infra.security.dto

data class UserPrincipal(
    var id: Long,
    var email: String,
    var role: String
)