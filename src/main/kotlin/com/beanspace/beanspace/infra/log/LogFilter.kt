package com.beanspace.beanspace.infra.log

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.web.util.WebUtils
import java.util.Enumeration
import java.util.UUID

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class LogFilter : OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        MDC.put("traceId", UUID.randomUUID().toString())
        log.info { "REQUEST TRACING_ID -> ${MDC.get("traceId")}" }

        val cachingRequestWrapper = ContentCachingRequestWrapper(request)
        val cachingResponseWrapper = ContentCachingResponseWrapper(response)

        val startTime = System.currentTimeMillis()
        filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper)
        val end = System.currentTimeMillis()

        try {
            log.info {
                HttpLogMessage.createInstance(
                    requestWrapper = cachingRequestWrapper,
                    responseWrapper = cachingResponseWrapper,
                    elapsedTime = (end - startTime) / 1000.0
                ).toPrettierLog()
            }
        } catch (e: Exception) {
            log.error(e) { "[${this::class.simpleName}] Logging 실패" }
        }
        cachingResponseWrapper.copyBodyToResponse()
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludePath = listOf(
            "/h2-console/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/v3/api-docs/",
            "/actuator/prometheus",
            "/healthcheck"
        )
        return excludePath.any { request.requestURI.startsWith(it) }
    }
}

private fun getHeaders(request: ContentCachingRequestWrapper): Map<Any, Any> {
    val headerMap: MutableMap<Any, Any> = HashMap()
    val headerArray: Enumeration<*> = request.headerNames
    while (headerArray.hasMoreElements()) {
        val headerName = headerArray.nextElement() as String
        headerMap[headerName] = request.getHeader(headerName)
    }
    return headerMap
}

data class HttpLogMessage(
    val httpMethod: String,
    val requestUri: String,
    val httpStatus: HttpStatus,
    val elapsedTime: Double,
    val headers: Map<Any, Any>,
    val queryString: String?,
    val requestBody: String?,
    val responseBody: String?,
) {
    companion object {
        fun createInstance(
            requestWrapper: ContentCachingRequestWrapper,
            responseWrapper: ContentCachingResponseWrapper,
            elapsedTime: Double
        ): HttpLogMessage {

            val request = WebUtils.getNativeRequest(
                requestWrapper,
                ContentCachingRequestWrapper::class.java
            )

            return HttpLogMessage(
                httpMethod = requestWrapper.method,
                requestUri = requestWrapper.requestURI,
                httpStatus = HttpStatus.valueOf(responseWrapper.status),
                elapsedTime = elapsedTime,
                headers = getHeaders(requestWrapper),
                queryString = requestWrapper.queryString,
                requestBody = request?.contentAsString,
                responseBody = responseWrapper.contentAsByteArray.toString(Charsets.UTF_8),
            )
        }
    }

    fun toPrettierLog(): String {
        return """
        |
        |[REQUEST] ${this.httpMethod} ${this.requestUri} ${this.httpStatus} (${this.elapsedTime}ms)
        |HEADERS: ${this.headers}
        |QUERY_STRING: ${this.queryString}
        |REQUEST_BODY: ${this.requestBody}
        |RESPONSE_BODY: ${this.responseBody}
        """.trimMargin()
    }
}