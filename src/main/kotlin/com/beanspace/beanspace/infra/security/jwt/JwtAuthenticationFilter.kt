package com.beanspace.beanspace.infra.security.jwt


import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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

        jwtPlugin.validateToken(jwt)
            .onSuccess {
                val memberId = it.payload.subject.toLong()
                val role = it.payload.get("role", String::class.java)
                val username = it.payload.get("username", String::class.java)
                val principal = UserPrincipal(memberId, username)
                //TODO JwtAuthenticationToken 정의하기
            }
    }

}