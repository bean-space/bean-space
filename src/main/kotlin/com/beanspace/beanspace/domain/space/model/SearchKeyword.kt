package com.beanspace.beanspace.domain.space.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class SearchKeyword(

    @Column
    val keyword: String,

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)