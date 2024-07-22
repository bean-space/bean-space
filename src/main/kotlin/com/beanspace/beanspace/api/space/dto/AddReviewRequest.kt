package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.space.model.Review
import com.beanspace.beanspace.domain.space.model.Space

data class AddReviewRequest(
    val content: String,
    val rating: Int,
    val reservationId: Long,
    val imageUrlList: List<String>
) {

    fun toEntity(member: Member, space: Space, reservation: Reservation): Review {
        return Review(
            content = this.content,
            rating = this.rating,
            member = member,
            space = space,
            reservation = reservation
        )
    }
}
