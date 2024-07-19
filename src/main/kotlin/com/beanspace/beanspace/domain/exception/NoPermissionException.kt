package com.beanspace.beanspace.domain.exception

data class NoPermissionException(
    private val text: String? = null
) : RuntimeException(
    "접근 권한이 없습니다. $text"
)