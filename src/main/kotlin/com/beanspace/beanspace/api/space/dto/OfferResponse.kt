package com.beanspace.beanspace.api.space.dto

import com.beanspace.beanspace.domain.space.model.Offer

data class OfferResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(offer: Offer): OfferResponse {
            return OfferResponse(
                id = offer.id!!,
                name = offer.name
            )
        }
    }
}
