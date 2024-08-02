package com.beanspace.beanspace.domain.reservation.repository

import com.beanspace.beanspace.domain.reservation.model.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.space.id = :spaceId AND r.isCancelled = :isCancelled AND r.checkOut >= :today")
    fun findAllBySpaceIdAndIsCancelledAndCheckOutAfter(
        spaceId: Long,
        isCancelled: Boolean,
        today: LocalDate
    ): List<Reservation>

    fun findByMemberIdAndCheckOutAfter(
        memberId: Long,
        date: LocalDate
    ): List<Reservation>
}