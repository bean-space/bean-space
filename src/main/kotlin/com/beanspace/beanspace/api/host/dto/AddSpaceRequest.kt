package com.beanspace.beanspace.api.host.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.space.model.Address
import com.beanspace.beanspace.domain.space.model.Space
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class AddSpaceRequest(

    @field:NotBlank(message = "숙소 이름은 1 ~ 30자까지 입력 가능합니다.")
    @field:Size(min = 1, max = 30, message = "숙소 이름은 1 ~ 30자까지 입력 가능합니다.")
    val listingName: String,

    @field:NotBlank(message = "숙소 소개글은 10 ~ 5000자까지 입력 가능합니다.")
    @field:Size(min = 10, max = 5000, message = "숙소 소개글은 10 ~ 5000자까지 입력 가능합니다.")
    val content: String,

    @field:NotEmpty(message = "이미지는 최소 1개에서 최대 5개까지 가능합니다.")
    @field:Size(min = 1, max = 5, message = "숙소 이미지는 최소 1개에서 최대 5개까지 가능합니다.")
    val imageUrlList: List<String>,

    val price: Int,
    val zipCode: String,
    val streetNameAddress: String,
    val detailedAddress: String,
    val sidoAndSigungu: String,
    val defaultPeople: Int,
    val maxPeople: Int,
    val pricePerPerson: Int,
    val bedRoomCount: Int,
    val bedCount: Int,
    val bathRoomCount: Int,
    val offer: List<Long>?
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
                    sidoAndSigungu = request.sidoAndSigungu
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