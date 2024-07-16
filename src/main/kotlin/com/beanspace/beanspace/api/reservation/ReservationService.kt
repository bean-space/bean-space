package com.beanspace.beanspace.api.reservation

import com.beanspace.beanspace.api.reservation.dto.ReservationRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val memberRepository: MemberRepository
) {

    fun reserveSpace(spaceId: Long, request: ReservationRequest /* 인증정보 */): ReservationResponse {
        val space = spaceRepository.findByIdOrNull(spaceId) ?: throw RuntimeException() // 수정필요
        val member = memberRepository.findByIdOrNull(1L) ?: throw RuntimeException() // 수정필요

        // 예약 날짜가 올바르게 들어왔는지 확인
        // 해당 날짜에 예약이 가능한지 확인

        return request.toEntity(space, member)
            .also { reservationRepository.save(it) }
            .let { ReservationResponse.from(it) }
    }

    fun cancelReservation(reservationId: Long /* 인증정보 */) {
        // 본인이 한 예약인지 확인
        // 예약 취소가 가능한 상태인지 확인
        reservationRepository.deleteById(reservationId)
    }
}