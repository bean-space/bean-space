package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Offer
import org.springframework.data.jpa.repository.JpaRepository

interface OfferRepository : JpaRepository<Offer, Long> {
    fun findByName(name: String): Offer?
}