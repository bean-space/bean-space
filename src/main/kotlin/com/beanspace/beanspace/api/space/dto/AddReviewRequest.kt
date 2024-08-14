package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.space.model.Review
import com.beanspace.beanspace.domain.space.model.Space
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddReviewRequest(
    @field:NotBlank(message = "리뷰는 1 ~ 3000까지 입력 가능합니다.")
    @field:Size(min = 1, max = 3000, message = "리뷰는 1 ~ 3000까지 입력 가능합니다")
    val content: String,

    @field:Min(value = 1, message = "별점은 1개 이상으로 입력해주세요.")
    @field:Max(value = 5, message = "별점은 5개 이하까지 가능합니다.")
    val rating: Int,

    @field:Size(max = 3, message = "리뷰 이미지는 최대 3개까지 가능합니다.")
    val imageUrlList: List<String>,

    val reservationId: Long,
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
