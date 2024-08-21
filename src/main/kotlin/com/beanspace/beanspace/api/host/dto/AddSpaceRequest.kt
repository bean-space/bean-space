package com.beanspace.beanspace.api.host.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.space.model.Address
import com.beanspace.beanspace.domain.space.model.Space
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

data class AddSpaceRequest(

    @field:NotBlank(message = "숙소 이름은 2 ~ 30자까지 입력 가능합니다.")
    @field:Size(min = 2, max = 30, message = "숙소 이름은 2 ~ 30자까지 입력 가능합니다.")
    val listingName: String,

    @field:NotBlank(message = "숙소 소개글은 10 ~ 5000자까지 입력 가능합니다.")
    @field:Size(min = 10, max = 5000, message = "숙소 소개글은 10 ~ 5000자까지 입력 가능합니다.")
    val content: String,

    @field:NotEmpty(message = "이미지는 최소 1개에서 최대 5개까지 가능합니다.")
    @field:Size(min = 1, max = 5, message = "숙소 이미지는 최소 1개에서 최대 5개까지 가능합니다.")
    val imageUrlList: List<String>,

    @field:Range(min = 1, max = 50000000, message = "1박당 가격은 최대 50000000원까지 가능합니다")
    val price: Int,
    val zipCode: String,
    val streetNameAddress: String,
    val detailedAddress: String,
    val sidoAndSigungu: String,
    @field:Range(min = 1, max = 99, message = "기본 인원은 최대 99명까지 가능합니다")
    val defaultPeople: Int,
    @field:Range(min = 1, max = 99, message = "최대 인원은 최대 99명까지 가능합니다")
    val maxPeople: Int,
    @field:Range(min = 1, max = 10000000, message = "인원당 추가 가격은 최대 10000000원까지 가능합니다")
    val pricePerPerson: Int,
    @field:Range(min = 0, max = 99, message = "침실 갯수는 0~99개만 가능합니다")
    val bedRoomCount: Int,
    @field:Range(min = 0, max = 99, message = "침대 갯수는 0~99개만 가능합니다")
    val bedCount: Int,
    @field:Range(min = 0, max = 99, message = "화장실 갯수는 0~99개만 가능합니다")
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