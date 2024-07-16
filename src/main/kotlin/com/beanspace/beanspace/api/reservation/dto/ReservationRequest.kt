package com.beanspace.beanspace.api.reservation.dto

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.space.model.Space
import java.time.LocalDate

data class ReservationRequest(
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val reservationPeople: Int,
    val couponId: Long
) {
    fun toEntity(space: Space, member: Member): Reservation {
        val cost = 1L // 가격 계산하기
        return Reservation(
            checkIn = checkIn,
            checkOut = checkOut,
            reservationPeople = reservationPeople,
            cost = cost,
            space = space,
            member = member
        )
    }
}