package com.beanspace.beanspace.api.image.dto

data class PostPhotoResponse(
    val title: String,
    val content: String,
    val imageUrlList: List<String>
)
