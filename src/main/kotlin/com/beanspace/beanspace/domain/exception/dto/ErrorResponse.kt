package com.beanspace.beanspace.domain.exception.dto

data class ErrorResponse(
    val code: String,
    val msg: String?
)