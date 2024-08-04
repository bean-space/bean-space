package com.beanspace.beanspace.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
    @GetMapping("/healthcheck")
    fun checkHealth(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}