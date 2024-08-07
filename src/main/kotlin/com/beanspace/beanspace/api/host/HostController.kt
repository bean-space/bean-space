package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.host.dto.AddSpaceRequest
import com.beanspace.beanspace.api.host.dto.UpdateSpaceRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAnyRole('HOST','ADMIN')")
@RestController
@RequestMapping("/api/v1/host")
class HostController(private val hostService: HostService) {

    @GetMapping("/spaces")
    fun getSpaceList(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<List<SpaceResponse>> {
        return ResponseEntity.ok(hostService.getSpaceList(userPrincipal.id))
    }

    @GetMapping("/reservations")
    fun getReservationList(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<List<ReservationResponse>> {
        return ResponseEntity.ok(hostService.getReservationList(userPrincipal.id))
    }

    @PostMapping("/spaces")
    fun createSpace(
        @Valid @RequestBody request: AddSpaceRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<SpaceResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostService.createSpace(request, userPrincipal.id))
    }

    @PutMapping("/spaces/{spaceId}")
    fun updateSpace(
        @PathVariable spaceId: Long,
        @Valid @RequestBody request: UpdateSpaceRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<SpaceResponse> {
        return ResponseEntity.ok(hostService.updateSpace(spaceId, request, userPrincipal.id))
    }

    @DeleteMapping("/spaces/{spaceId}")
    fun deleteSpace(
        @PathVariable spaceId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        hostService.deleteSpace(spaceId, userPrincipal.id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}