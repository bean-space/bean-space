package com.beanspace.beanspace.domain.member.repository

import com.beanspace.beanspace.domain.member.model.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: String): Member

    fun existsByEmail(email: String): Boolean
}