package com.beanspace.beanspace.domain.space.controller

import com.beanspace.beanspace.domain.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.space.service.SpaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/spaces")
class SpaceController(private val spaceService: SpaceService) {

    @GetMapping
    fun getSpaceList(): ResponseEntity<List<SpaceResponse>> {
        //TODO 필터링 적용
        return ResponseEntity.ok(spaceService.getSpaceList())
    }

    @GetMapping("/{spaceId}")
    fun getSpace(@PathVariable spaceId: Long): ResponseEntity<SpaceResponse> {
        return ResponseEntity.ok(spaceService.getSpace(spaceId))
    }
}