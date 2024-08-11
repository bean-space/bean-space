package com.beanspace.beanspace.domain.space.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
import com.beanspace.beanspace.domain.member.model.Member
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    val host: Member,

    @Column
    var isDeleted: Boolean = false,

    @Column
    var deletedAt: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : BaseTimeEntity() {
    fun isProceeded(): Boolean {
        return status != SpaceStatus.PENDING
    }

    fun active() {
        status = SpaceStatus.ACTIVE
    }

    fun reject() {
        status = SpaceStatus.REJECTED
    }

    fun update(
        listingName: String,
        price: Int,
        content: String,
        defaultPeople: Int,
        maxPeople: Int,
        pricePerPerson: Int,
        bedRoomCount: Int,
        bedCount: Int,
        bathRoomCount: Int
    ) {
        this.listingName = listingName
        this.price = price
        this.content = content
        this.defaultPeople = defaultPeople
        this.maxPeople = maxPeople
        this.pricePerPerson = pricePerPerson
        this.bedRoomCount = bedRoomCount
        this.bedCount = bedCount
        this.bathRoomCount = bathRoomCount
    }

    fun hasPermission(hostId: Long): Boolean {
        return this.host.id == hostId
    }

    fun delete() {
        this.isDeleted = true
        this.deletedAt = LocalDateTime.now()
    }

    fun calculateTotalCost(headCount: Int, stayDays: Long): Long {
        return (this.price + (headCount - this.defaultPeople).coerceAtLeast(0) * this.pricePerPerson) * stayDays
    }
}