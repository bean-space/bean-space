package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.space.model.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findAllBySpaceId(spaceId: Long, pageable: Pageable): Page<Review>
    fun existsByReservation(reservation: Reservation): Boolean

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.space.id = ?1")
    fun getAverageRating(spaceId: Long): Double?
}