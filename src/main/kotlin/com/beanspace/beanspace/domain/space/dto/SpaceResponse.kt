package com.beanspace.beanspace.domain.space.dto

import com.beanspace.beanspace.domain.space.model.Space

data class SpaceResponse(
    val id: Long,
    val listingName: String,
    val price: Int,
    val streetNameAddress: String,
    val detailedAddress: String,
    val sido: String,
    val content: String,
    val defaultPeople: Int,
    val maxPeople: Int,
    val pricePerPerson: Int,
    val bedRoomCount: Int,
    val bedCount: Int,
    val bathRoomCount: Int,
    //TODO val imageUrlList: List<String>

) {
    companion object {
        fun from(space: Space): SpaceResponse {
            return SpaceResponse(
                id = space.id!!,
                listingName = space.listingName,
                price = space.price,
                streetNameAddress = space.address.streetNameAddress,
                detailedAddress = space.address.detailedAddress,
                sido = space.address.sido,
                content = space.content,
                defaultPeople = space.defaultPeople,
                maxPeople = space.maxPeople,
                pricePerPerson = space.pricePerPerson,
                bedRoomCount = space.bedRoomCount,
                bedCount = space.bedCount,
                bathRoomCount = space.bathRoomCount
            )
        }
    }
}