package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.space.model.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long>, ReviewQueryDslRepository {
    fun findBySpaceId(spaceId: Long): List<Review>
    fun existsByReservation(reservation: Reservation): Boolean
}