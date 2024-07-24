package com.beanspace.beanspace.domain.member.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

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

    @Column
    var profileImageUrl: String?,

    @Enumerated(EnumType.STRING)
    @Column
    var role: MemberRole = MemberRole.MEMBER,

    @Column(nullable = true)
    var provider: String? = null,

    @Column(nullable = true)
    var providerId: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

) : BaseTimeEntity() {
    fun updateProfile(nickname: String, email: String, profileImageUrl: String?) {
        this.nickname = nickname
        this.email = email
        this.profileImageUrl = profileImageUrl
    }

    fun updateRoleToHost() {
        this.role = MemberRole.HOST
    }
}