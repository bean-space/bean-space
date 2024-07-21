package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Space

data class WishListedSpaceResponse(
    val spaceId: Long,
    val listingName: String,
    val price: Int,
    val sido: String,
    val imageUrlList: List<String>
) {
    companion object {
        fun fromEntity(space: Space, imageUrlList: List<String>): WishListedSpaceResponse {
            return WishListedSpaceResponse(
                spaceId = space.id!!,
                listingName = space.listingName,
                price = space.price,
                sido = space.address.sido,
                imageUrlList = imageUrlList
            )
        }
    }
}