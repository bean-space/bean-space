package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.SpaceOffer
import org.springframework.data.jpa.repository.JpaRepository

interface SpaceOfferRepository : JpaRepository<SpaceOffer, Long> {
    fun deleteAllBySpaceId(spaceId: Long)
    fun findAllBySpaceId(spaceId: Long): List<SpaceOffer>
}