package com.beanspace.beanspace.domain.exception

data class AccessDeniedException(
    private val text: String
) : RuntimeException(
    "접근이 거부되었습니다.: $text"
)