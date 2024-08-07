package com.beanspace.beanspace.domain.space.model

import com.beanspace.beanspace.domain.common.BaseTimeEntity
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.reservation.model.Reservation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLRestriction("isDeleted = false")
class Review(
    @Column
    var content: String,

    @Column
    var rating: Int,

    @Column
    var isDeleted: Boolean = false,

    @Column
    var deletedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    val space: Space,

    @OneToOne
    @JoinColumn(name = "reservation_id")
    val reservation: Reservation,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseTimeEntity() {

    fun update(content: String, rating: Int) {
        this.content = content
        this.rating = rating
    }

    fun delete() {
        isDeleted = true
        deletedAt = LocalDateTime.now()
    }
}
