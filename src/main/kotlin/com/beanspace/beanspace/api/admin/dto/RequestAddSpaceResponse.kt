package com.beanspace.beanspace.api.admin.dto

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus

data class RequestAddSpaceResponse(
    val id: Long,
    val listingName: String,
    val status: SpaceStatus,
    val hostId: Long,
    val hostNickname: String
) {
    companion object {
        fun from(space: Space): RequestAddSpaceResponse {
            return RequestAddSpaceResponse(
                space.id!!,
                space.listingName,
                space.status,
                space.host.id!!,
                space.host.nickname
            )
        }
    }
}
