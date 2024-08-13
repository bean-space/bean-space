package com.beanspace.beanspace.infra.security.jwt


import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtPlugin: JwtPlugin
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = request.getBearerToken()

        if (jwt is String) {
            jwtPlugin.validateToken(jwt)
                .onSuccess {
                    val memberId = it.payload.subject.toLong()
                    val role = it.payload.get("role", String::class.java)

                    val principal = UserPrincipal(
                        id = memberId,
                        roles = setOf(role)
                    )

                    val authentication = JwtAuthenticationToken(
                        principal = principal,
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    )

                    SecurityContextHolder.getContext().authentication = authentication
                }
                .onFailure { exception ->
                    when (exception) {
                        is ExpiredJwtException -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired")
                        }

                        else -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                        }
                    }

                    return
                }
        }

        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.getBearerToken(): String? {
        val headerValue = this.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return Regex("^Bearer (.+?)$").find(headerValue)?.groupValues?.get(1)
    }
}