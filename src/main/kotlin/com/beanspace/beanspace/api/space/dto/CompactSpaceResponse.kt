package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Space
import java.io.Serializable

data class CompactSpaceResponse(
    val spaceId: Long,
    val listingName: String,
    val price: Int,
    val defaultPeople: Int,
    val sidoAndSigungu: String,
    val imageUrlList: List<String>
) : Serializable {
    companion object {
        fun fromEntity(space: Space, imageUrlList: List<String>): CompactSpaceResponse {
            return CompactSpaceResponse(
                spaceId = space.id!!,
                listingName = space.listingName,
                price = space.price,
                defaultPeople = space.defaultPeople,
                sidoAndSigungu = space.address.sidoAndSigungu,
                imageUrlList = imageUrlList
            )
        }
    }
}