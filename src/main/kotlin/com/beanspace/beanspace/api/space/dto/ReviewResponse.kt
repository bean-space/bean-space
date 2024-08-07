package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Review
import java.time.LocalDateTime

data class ReviewResponse(
    val id: Long,
    var content: String,
    var rating: Int,
    val createdAt: LocalDateTime,
    val reviewerId: Long,
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
                createdAt = review.createdAt!!,
                reviewerId = review.member.id!!,
                reviewerName = review.member.nickname,
                reviewerProfileUrl = review.member.profileImageUrl,
                imageUrlList = imageUrlList
            )
        }
    }
}
