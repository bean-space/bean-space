package com.beanspace.beanspace.api.member.dto

import com.beanspace.beanspace.domain.reservation.model.Reservation
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberReservationResponse(
    val reservationId: Long,
    val listingName: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val reservationPeople: Int,
    val cost: Long,
    val createdAt: LocalDateTime,
    val isReviewed: Boolean,
    val isCancelled: Boolean,
    val zipCode: String,
    val streetNameAddress: String,
    val detailedAddress: String,
    val spaceId: Long
) {
    companion object {
        fun from(
            reservation: Reservation,
            isReviewed: Boolean
        ): MemberReservationResponse {
            return MemberReservationResponse(
                reservationId = reservation.id!!,
                listingName = reservation.space.listingName,
                checkIn = reservation.checkIn,
                checkOut = reservation.checkOut,
                reservationPeople = reservation.reservationPeople,
                cost = reservation.cost,
                createdAt = reservation.createdAt!!,
                isReviewed = isReviewed,
                isCancelled = reservation.isCancelled,
                zipCode = reservation.space.address.zipCode,
                streetNameAddress = reservation.space.address.streetNameAddress,
                detailedAddress = reservation.space.address.detailedAddress,
                spaceId = reservation.space.id!!
            )
        }
    }
}