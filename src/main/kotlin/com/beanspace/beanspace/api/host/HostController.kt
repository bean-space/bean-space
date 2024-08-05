package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.host.dto.AddSpaceRequest
import com.beanspace.beanspace.api.host.dto.UpdateSpaceRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        @RequestBody request: AddSpaceRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<SpaceResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostService.createSpace(request, userPrincipal.id))
    }

    @PutMapping("/spaces/{spaceId}")
    fun updateSpace(
        @PathVariable spaceId: Long,
        @RequestBody request: UpdateSpaceRequest,
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