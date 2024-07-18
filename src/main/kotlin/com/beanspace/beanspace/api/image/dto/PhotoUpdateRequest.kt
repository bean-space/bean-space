package com.beanspace.beanspace.api.image.dto

data class PhotoUpdateRequest(
    val photoId: Long?, // 새로운 이미지인 경우 null
    val orderIndex: Int,
    val fileName: String? // 수정하지 않을 이미지는 null
)