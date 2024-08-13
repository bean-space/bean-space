package com.beanspace.beanspace.infra.security.jwt

import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetails
import java.io.Serializable

class JwtAuthenticationToken(
    private val principal: UserPrincipal,
    details: WebAuthenticationDetails
) : AbstractAuthenticationToken(principal.authorities), Serializable {

    init {
        super.setAuthenticated(true)
        super.setDetails(details)

    }

    override fun getCredentials() = null

    override fun getPrincipal() = principal

    override fun isAuthenticated() = true

}