package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pai/v1/host")
class HostController(private val hostService: HostService) {

    @PostMapping("/space")
    fun createSpace(): ResponseEntity<SpaceResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostService.createSpace())
    }

    @PutMapping("/space/{spaceId}")
    fun updateSpace(@PathVariable spaceId: Long): ResponseEntity<SpaceResponse> {
        return ResponseEntity.ok(hostService.updateSpace(spaceId))
    }

    @DeleteMapping("/space/{spaceId}")
    fun deleteSpace(@PathVariable spaceId: Long): ResponseEntity<Unit> {
        hostService.deleteSpace(spaceId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}