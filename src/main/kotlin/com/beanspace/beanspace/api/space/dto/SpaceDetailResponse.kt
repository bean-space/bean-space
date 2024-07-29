package com.beanspace.beanspace.api.space.dto

import java.time.LocalDate

data class SpaceDetailResponse(
    val space: SpaceResponse,
    val averageRating: Double,
    val host: HostResponse,
    val reservedDateList: List<LocalDate>,
) {
    companion object {
        fun from(
            spaceResponse: SpaceResponse,
            averageRating: Double,
            hostResponse: HostResponse,
            reservedDateList: List<LocalDate>,
        ): SpaceDetailResponse {
            return SpaceDetailResponse(
                space = spaceResponse,
                averageRating = averageRating,
                host = hostResponse,
                reservedDateList = reservedDateList
            )
        }
    }
}