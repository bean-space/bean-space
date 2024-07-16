package com.beanspace.beanspace.domain.exception

data class InvalidImageException(
    val msg: String
) : RuntimeException(
    msg
)