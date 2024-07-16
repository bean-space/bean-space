package com.beanspace.beanspace.infra.security.jwt


import com.beanspace.beanspace.infra.security.dto.UserPrincipal
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

        jwt?.let { token ->
            jwtPlugin.validateToken(jwt)
                .onSuccess { decoded ->
                    val memberId = decoded.payload.subject.toLong()
                    val role = decoded.payload.get("role", String::class.java)
                    val email = decoded.payload.get("email", String::class.java)

                    val principal = UserPrincipal(memberId, email, role)
                    val detail = WebAuthenticationDetailsSource().buildDetails(request)
                    val auth = JwtAuthenticationToken(principal, detail)

                    SecurityContextHolder.getContext().authentication = auth
                }
        }
        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.getBearerToken(): String? {
        val headerValue = this.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return Regex("^Bearer (.+?)$").find(headerValue)?.groupValues?.get(1)
    }
}