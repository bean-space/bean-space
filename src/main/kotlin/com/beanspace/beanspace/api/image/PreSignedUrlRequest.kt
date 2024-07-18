package com.beanspace.beanspace.api.image

data class PreSignedUrlRequest(
    val fileName: String,
    val contentType: String
)