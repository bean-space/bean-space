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
    val role: MemberRole = MemberRole.MEMBER,

    @Column
    var phoneNumber: String,


    ) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}