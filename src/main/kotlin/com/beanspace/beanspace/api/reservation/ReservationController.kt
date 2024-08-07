package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@PreAuthorize("hasAnyRole('MEMBER','HOST')")
@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping
    fun reserveSpace(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable spaceId: Long,
        @RequestBody request: ReservationRequest
    ): ResponseEntity<ReservationResponse> {
        return ResponseEntity.ok(reservationService.reserveSpace(principal.id, spaceId, request))
    }

    @DeleteMapping("{reservationId}")
    fun cancelReservation(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable reservationId: Long
    ): ResponseEntity<Unit> {
        reservationService.cancelReservation(principal.id, reservationId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}