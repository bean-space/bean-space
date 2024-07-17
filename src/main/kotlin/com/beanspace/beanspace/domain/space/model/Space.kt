package com.beanspace.beanspace.domain.space.model

import com.beanspace.beanspace.domain.member.model.Member
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Space(
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
    @Enumerated(EnumType.STRING)
    var status: SpaceStatus = SpaceStatus.PENDING,

    @ManyToOne
    @JoinColumn(name = "host_id")
    val host: Member,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)