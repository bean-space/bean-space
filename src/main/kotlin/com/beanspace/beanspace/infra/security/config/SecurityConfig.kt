package com.beanspace.beanspace.infra.security.config

import com.beanspace.beanspace.infra.security.CustomAccessDeniedHandler
import com.beanspace.beanspace.infra.security.CustomAuthenticationEntrypoint
import com.beanspace.beanspace.infra.security.jwt.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val customAuthenticationEntrypoint: CustomAuthenticationEntrypoint
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { cors ->
                cors.configurationSource {
                    val configuration = CorsConfiguration()
                    configuration.allowedOrigins =
                        listOf(
                            "http://localhost:5173",
                            "https://bean-space-front.vercel.app/",
                            "https://www.thebean.space/"
                        )
                    configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    configuration.allowedHeaders = listOf("*")
                    configuration.allowCredentials = true
                    configuration
                }
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .headers { header -> header.frameOptions { it.disable() } }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/spaces",
                        "/api/v1/spaces/**",
                        "/api/v1/coupons",
                        "/api/v1/spaces/offer"
                    )
                    .permitAll()
                    .requestMatchers(
                        "/healthcheck",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/v1/auth/sign-up",
                        "/api/v1/auth/login",
                        "/error",
                        "/api/v1/spaces/**",
                        "/oauth2/login/**",
                        "/actuator/prometheus",
                        "/api/v1/images/presigned-url"
                    ).permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntrypoint)
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .build()
    }
}

