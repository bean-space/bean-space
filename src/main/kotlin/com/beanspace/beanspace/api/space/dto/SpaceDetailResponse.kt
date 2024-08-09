package com.beanspace.beanspace.api.space.dto

import java.time.LocalDate

data class SpaceDetailResponse(
    val space: SpaceResponseWithoutAddress,
    val averageRating: Double,
    val host: HostResponse,
    val offerList: List<Long>,
    val reservedDateList: List<LocalDate>,
) {
    companion object {
        fun from(
            spaceResponseWithoutAddress: SpaceResponseWithoutAddress,
            averageRating: Double,
            hostResponse: HostResponse,
            offerList: List<Long>,
            reservedDateList: List<LocalDate>,
        ): SpaceDetailResponse {
            return SpaceDetailResponse(
                space = spaceResponseWithoutAddress,
                averageRating = averageRating,
                host = hostResponse,
                offerList = offerList,
                reservedDateList = reservedDateList
            )
        }
    }
}