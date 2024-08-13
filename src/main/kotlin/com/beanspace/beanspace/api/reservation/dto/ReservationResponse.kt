package com.beanspace.beanspace.api.reservation.dto

import com.beanspace.beanspace.domain.reservation.model.Reservation
import java.time.LocalDate
import java.time.LocalDateTime

data class ReservationResponse(
    val id: Long,
    val listingName: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val reservationPeople: Int,
    val cost: Long,
    val createdAt: LocalDateTime,
    val spaceId: Long
) {
    companion object {
        fun from(reservation: Reservation): ReservationResponse {
            return ReservationResponse(
                id = reservation.id!!,
                listingName = reservation.space.listingName,
                checkIn = reservation.checkIn,
                checkOut = reservation.checkOut,
                reservationPeople = reservation.reservationPeople,
                cost = reservation.cost,
                createdAt = reservation.createdAt!!,
                spaceId = reservation.space.id!!
            )
        }
    }
}