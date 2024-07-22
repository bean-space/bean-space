package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Review
import com.querydsl.core.annotations.QueryProjection

data class ReviewResponse @QueryProjection constructor(
    val id: Long,
    var content: String,
    var rating: Int,
    val reviewerName: String,
    val reviewerProfileUrl: String? = null,
    var imageUrlList: List<String>? = null
) {
    companion object {
        fun from(review: Review, imageUrlList: List<String>): ReviewResponse {
            return ReviewResponse(
                id = review.id!!,
                content = review.content,
                rating = review.rating,
                reviewerName = review.member.nickname,
                reviewerProfileUrl = "",
                imageUrlList = imageUrlList
            )
        }
    }
}
