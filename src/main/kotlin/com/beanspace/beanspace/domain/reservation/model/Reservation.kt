package com.beanspace.beanspace.domain.reservation.model

import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.space.model.Space
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
class Reservation(
    @Column
    val checkIn: LocalDate,

    @Column
    val checkOut: LocalDate,

    @Column
    val reservationPeople: Int,

    @Column
    val cost: Long,

    @Column
    var isCancelled: Boolean = false,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    val space: Space,

    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    fun validateOwner(memberId: Long) = (member.id == memberId)

    fun isBeforeCancellationDeadline(): Boolean {
        val today = LocalDate.now()

        // 예약 취소는 체크인 날짜 이틀 전까지 가능
        val cancellationDeadline = checkIn.minusDays(2)

        return today <= cancellationDeadline
    }

    fun isActiveReservation() = !isCancelled

    fun cancelReservation() {
        isCancelled = true
    }

    fun isReviewAllowed(): Boolean {
        return LocalDateTime.now().isAfter(LocalDateTime.of(checkOut, LocalTime.NOON))
    }
}