package com.beanspace.beanspace.domain.member.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
import jakarta.persistence.*


@Entity
class Member(

    @Column
    val phoneNumber: String,

    @Column
    var password: String,

    @Column
    var nickname: String,

    @Column
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column
    var role: MemberRole = MemberRole.MEMBER,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

) : BaseTimeEntity()