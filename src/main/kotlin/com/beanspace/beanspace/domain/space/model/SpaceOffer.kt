package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.*

@Entity
class SpaceOffer(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    val space: Space,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    val offer: Offer,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
