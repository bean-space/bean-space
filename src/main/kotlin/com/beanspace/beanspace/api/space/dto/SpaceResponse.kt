package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus

data class SpaceResponse(
    val id: Long,
    val listingName: String,
    val price: Int,
    val zipCode: String,
    val streetNameAddress: String,
    val detailedAddress: String,
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
        ): SpaceResponse {
            return SpaceResponse(
                id = space.id!!,
                listingName = space.listingName,
                price = space.price,
                zipCode = space.address.zipCode,
                streetNameAddress = space.address.streetNameAddress,
                detailedAddress = space.address.detailedAddress,
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