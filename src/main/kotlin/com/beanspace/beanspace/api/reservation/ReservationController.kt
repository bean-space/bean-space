package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping
    fun reserveSpace(
        @PathVariable spaceId: Long,
        @RequestBody request: ReservationRequest /*인증정보*/
    ): ResponseEntity<ReservationResponse> {
        return ResponseEntity.ok(reservationService.reserveSpace(spaceId, request /* 인증정보 */))
    }

    @DeleteMapping("{reservationId}")
    fun cancelReservation(@PathVariable reservationId: Long /*인증정보*/): ResponseEntity<Unit> {
        reservationService.cancelReservation(reservationId /* 인증정보 */)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}