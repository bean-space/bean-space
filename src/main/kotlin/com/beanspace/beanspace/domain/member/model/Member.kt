package com.beanspace.beanspace.domain.member.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
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
    var role: MemberRole = MemberRole.MEMBER,

    @Column
    val phoneNumber: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

) : BaseTimeEntity()