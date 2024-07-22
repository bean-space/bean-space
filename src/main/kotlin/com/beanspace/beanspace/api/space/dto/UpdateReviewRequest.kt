package com.beanspace.beanspace.api.space.dto

data class UpdateReviewRequest(
    val content: String,
    val rating: Int,
    val imageUrlList: List<String>
)