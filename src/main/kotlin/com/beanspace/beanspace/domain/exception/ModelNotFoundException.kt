package com.beanspace.beanspace.domain.exception

data class ModelNotFoundException(
    val model: String,
    val id: Any,
) : RuntimeException(
    "입력받은 id: ${id}에 대한 ${model}을 찾을 수 없습니다."
)