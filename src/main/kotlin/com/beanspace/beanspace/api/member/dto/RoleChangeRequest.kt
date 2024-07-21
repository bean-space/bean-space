package com.beanspace.beanspace.api.member.dto

import com.beanspace.beanspace.domain.member.model.MemberRole

data class RoleChangeRequest(
    val requestedRole: MemberRole
)