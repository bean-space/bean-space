package com.beanspace.beanspace.infra.security.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal(
    val id: Long,
    val authorities: Collection<GrantedAuthority>
) {
    constructor(id: Long, roles: Set<String>) : this(
        id,
        roles.map { SimpleGrantedAuthority("ROLE_$it") }
    )
}