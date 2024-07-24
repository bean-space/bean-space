package com.beanspace.beanspace.api.image.dto

data class PreSignedUrlRequest(
    val fileName: String,
    val contentType: String,
    val imageType: String
)