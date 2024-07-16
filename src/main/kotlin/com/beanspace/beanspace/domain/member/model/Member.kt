package com.beanspace.beanspace.domain.member.model

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
class Member(

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column
    val role: MemberRole = MemberRole.MEMBER,

    @Column(nullable = false)
    var phoneNumber: Int,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),


    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}