package com.beanspace.beanspace.domain.member.repository

import com.beanspace.beanspace.domain.member.model.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByPhoneNumber(phoneNumber: String): Member?

    fun existsByPhoneNumber(phoneNumber: String): Boolean

    fun findByProviderAndProviderId(provider: String, providerId: String): Member?
}