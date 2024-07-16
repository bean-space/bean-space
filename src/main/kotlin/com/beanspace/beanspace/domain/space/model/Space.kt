package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Space(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var listingName: String,

    @Column
    var price: Int,

    @Embedded
    val address: Address,

    @Column
    var content: String,

    @Column
    var defaultPeople: Int,

    @Column
    var maxPeople: Int,

    @Column
    var pricePerPerson: Int,

    @Column
    var bedRoomCount: Int,

    @Column
    var bedCount: Int,

    @Column
    var bathRoomCount: Int,

    @Column
    var status: SpaceStatus = SpaceStatus.PENDING,

    )