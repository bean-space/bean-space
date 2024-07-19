package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/spaces")
class SpaceController(private val spaceService: SpaceService) {

    @GetMapping
    fun getSpaceList(
        @RequestParam(required = false) sido: String?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") checkIn: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") checkOut: LocalDate?,
        @RequestParam(required = false) headCount: Int?,
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
    ): ResponseEntity<Page<SpaceResponse>> {
        return ResponseEntity.ok(
            spaceService.getSpaceList(
                sido = sido,
                checkIn = checkIn,
                checkOut = checkOut,
                headCount = headCount,
                pageable = pageable,
            )
        )
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
    fun addSpaceToWishList(
        @PathVariable spaceId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(spaceService.addToWishList(spaceId, userPrincipal))
    }

    @DeleteMapping("/{spaceId}/wishlist")
    fun deleteSpaceFromWishList(
        @PathVariable spaceId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(spaceService.deleteFromWishList(spaceId, userPrincipal))
    }
}