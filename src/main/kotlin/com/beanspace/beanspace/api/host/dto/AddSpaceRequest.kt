package com.beanspace.beanspace.api.host.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.space.model.Address
import com.beanspace.beanspace.domain.space.model.Space

data class AddSpaceRequest(
    val listingName: String,
    val price: Int,
    val zipCode: String,
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
    val imageUrlList: List<String>
) {
    companion object {
        fun toEntity(request: AddSpaceRequest, host: Member): Space {
            return Space(
                listingName = request.listingName,
                price = request.price,
                address = Address(
                    zipCode = request.zipCode,
                    streetNameAddress = request.streetNameAddress,
                    detailedAddress = request.detailedAddress,
                    sido = request.sido
                ),
                content = request.content,
                defaultPeople = request.defaultPeople,
                maxPeople = request.maxPeople,
                pricePerPerson = request.pricePerPerson,
                bedRoomCount = request.bedRoomCount,
                bedCount = request.bedCount,
                bathRoomCount = request.bathRoomCount,
                host = host
            )
        }
    }
}