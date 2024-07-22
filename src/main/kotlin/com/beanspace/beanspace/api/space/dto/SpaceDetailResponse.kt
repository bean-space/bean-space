package com.beanspace.beanspace.api.space.dto

import java.time.LocalDate

data class SpaceDetailResponse(
    val space: SpaceResponse,
    val reservedDateList: List<LocalDate>,
    val reviewList: List<ReviewResponse>,
) {
    companion object {
        fun from(
            spaceResponse: SpaceResponse,
            reservedDateList: List<LocalDate>,
            reviewList: List<ReviewResponse>
        ): SpaceDetailResponse {
            return SpaceDetailResponse(
                space = spaceResponse,
                reservedDateList = reservedDateList,
                reviewList = reviewList
            )
        }
    }
}