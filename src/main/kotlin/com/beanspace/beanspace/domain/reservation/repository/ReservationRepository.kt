package com.beanspace.beanspace.domain.reservation.repository

import com.beanspace.beanspace.domain.reservation.model.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long>