package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class SpaceOffer(

    @ManyToOne
    @JoinColumn(name = "space_id")
    val space: Space,

    @ManyToOne
    @JoinColumn(name = "offer_id")
    val offer: Offer,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)