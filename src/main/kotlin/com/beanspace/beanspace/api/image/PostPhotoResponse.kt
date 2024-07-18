package com.beanspace.beanspace.api.image

data class PostPhotoResponse(
    val title: String,
    val content: String,
    val imageUrlList: List<String>
)
