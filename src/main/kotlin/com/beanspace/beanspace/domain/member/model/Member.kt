package com.beanspace.beanspace.domain.member.model

import jakarta.persistence.*


@Entity
class Member(

    @Column
    var email: String,

    @Column
    var password: String,

    @Column
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column
    val role: MemberRole = MemberRole.MEMBER,

    @Column
    var phoneNumber: String,


    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}