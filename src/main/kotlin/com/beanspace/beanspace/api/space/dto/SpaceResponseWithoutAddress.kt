package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus

data class SpaceResponseWithoutAddress(
    val id: Long,
    val listingName: String,
    val price: Int,
    val sidoAndSigungu: String,
    val content: String,
    val defaultPeople: Int,
    val maxPeople: Int,
    val pricePerPerson: Int,
    val bedRoomCount: Int,
    val bedCount: Int,
    val bathRoomCount: Int,
    val status: SpaceStatus,
    val imageUrlList: List<String>
) {
    companion object {
        fun from(
            space: Space,
            imageUrlList: List<String>,
        ): SpaceResponseWithoutAddress {
            return SpaceResponseWithoutAddress(
                id = space.id!!,
                listingName = space.listingName,
                price = space.price,
                sidoAndSigungu = space.address.sidoAndSigungu,
                content = space.content,
                defaultPeople = space.defaultPeople,
                maxPeople = space.maxPeople,
                pricePerPerson = space.pricePerPerson,
                bedRoomCount = space.bedRoomCount,
                bedCount = space.bedCount,
                bathRoomCount = space.bathRoomCount,
                status = space.status,
                imageUrlList = imageUrlList
            )
        }
    }
}