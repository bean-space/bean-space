package com.beanspace.beanspace.domain.image.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "image")
data class Image(
    @Enumerated(EnumType.STRING)
    val type: ImageType,

    @Column
    var imageUrl: String,

    @Column
    val contentId: Long,

    @Column
    var orderIndex: Int = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity()

