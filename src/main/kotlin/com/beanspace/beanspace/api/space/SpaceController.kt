package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.AddReviewRequest
import com.beanspace.beanspace.api.space.dto.ReviewResponse
import com.beanspace.beanspace.api.space.dto.SpaceDetailResponse
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.api.space.dto.UpdateReviewRequest
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
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
    fun getSpace(@PathVariable spaceId: Long): ResponseEntity<SpaceDetailResponse> {
        return ResponseEntity.ok(spaceService.getSpace(spaceId, LocalDate.now()))
    }

    @PostMapping("/{spaceId}/reviews")
    fun addReview(
        @PathVariable spaceId: Long,
        @RequestBody request: AddReviewRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(spaceService.addReview(spaceId, request, userPrincipal.id))
    }

    @GetMapping("/{spaceId}/reviews")
    fun getAllReviews(
        @PathVariable spaceId: Long,
        @PageableDefault(page = 0, size = 5, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<Page<ReviewResponse>> {
        return ResponseEntity.ok(spaceService.getReviews(spaceId, pageable))
    }

    @PutMapping("/{spaceId}/reviews/{reviewId}")
    fun updateReview(
        @PathVariable spaceId: Long,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        spaceService.updateReview(
            spaceId = spaceId,
            reviewId = reviewId,
            request = request,
            userPrincipal = userPrincipal
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{spaceId}/reviews/{reviewId}")
    fun deleteReview(
        @PathVariable spaceId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        spaceService.deleteReview(spaceId, reviewId, userPrincipal)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
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