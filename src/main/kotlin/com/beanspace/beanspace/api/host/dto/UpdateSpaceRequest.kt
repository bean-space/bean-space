package com.beanspace.beanspace.api.host.dto

data class UpdateSpaceRequest(
    val listingName: String,
    val price: Int,
    val content: String,
    val defaultPeople: Int,
    val maxPeople: Int,
    val pricePerPerson: Int,
    val bedRoomCount: Int,
    val bedCount: Int,
    val bathRoomCount: Int,
    val imageUrlList: List<String>
)
