package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "wishlist")
class Wishlist(
    @Column
    val spaceId: Long,

    @Column
    val memberId: Long,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)