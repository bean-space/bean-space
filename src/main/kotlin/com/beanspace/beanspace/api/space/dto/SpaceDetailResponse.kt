package com.beanspace.beanspace.api.space.dto

import java.time.LocalDate

data class SpaceDetailResponse(
    val space: SpaceResponse,
    val host: HostResponse,
    val reservedDateList: List<LocalDate>,
) {
    companion object {
        fun from(
            spaceResponse: SpaceResponse,
            hostResponse: HostResponse,
            reservedDateList: List<LocalDate>,
        ): SpaceDetailResponse {
            return SpaceDetailResponse(
                space = spaceResponse,
                reservedDateList = reservedDateList,
                host = hostResponse,
                reservedDateList = reservedDateList
            )
        }
    }
}