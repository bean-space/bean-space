package com.beanspace.beanspace.api.admin.dto

data class RequestAddSpaceResponse(
    val id: Long,
    val listingName: String,
    val status: String,
    val hostId: Long,
    val hostNickname: String
)
