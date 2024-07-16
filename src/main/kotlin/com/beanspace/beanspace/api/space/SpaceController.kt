package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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

    @PostMapping("/{spaceId}/review")
    fun addReview(@PathVariable spaceId: Long /*TODO request 받기 */): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.CREATED).body(spaceService.addReview(spaceId))
    }

    @PostMapping("/{spaceId}/wishlist")
    fun addSpaceToWishList(@PathVariable spaceId: Long /*인증 정보*/): ResponseEntity<Unit> {
        return ResponseEntity.ok(spaceService.addToWishList(spaceId))
    }

    @DeleteMapping("/{spaceId}/wishlist")
    fun deleteSpaceFromWishList(@PathVariable spaceId: Long /*인증 정보*/): ResponseEntity<Unit> {
        return ResponseEntity.ok(spaceService.deleteFromWishList(spaceId))
    }
}