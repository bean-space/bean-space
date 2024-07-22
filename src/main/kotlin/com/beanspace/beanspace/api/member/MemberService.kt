package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.auth.dto.LoginResponse
import com.beanspace.beanspace.api.coupon.dto.UserCouponResponse
import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.api.space.dto.WishListedSpaceResponse
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import com.beanspace.beanspace.infra.security.jwt.JwtPlugin
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val userCouponRepository: UserCouponRepository,
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val jwtPlugin: JwtPlugin
) {

    @Transactional
    fun updateProfile(principal: UserPrincipal, request: UpdateProfileRequest): MemberProfileResponse {

        return memberRepository.findByIdOrNull(principal.id)
            ?.also {
                it.updateProfile(
                    nickname = request.nickname,
                    email = request.email,
                    profileImageUrl = request.profileImageUrl
                )
            }
            ?.let { MemberProfileResponse.fromEntity(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }

    fun getProfile(principal: UserPrincipal): MemberProfileResponse {

        return memberRepository.findByIdOrNull(principal.id)
            ?.let { MemberProfileResponse.fromEntity(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }

    @Transactional
    fun updateRoleToHost(principal: UserPrincipal): LoginResponse {

        return memberRepository.findByIdOrNull(principal.id)
            ?.also {
                check(it.role == MemberRole.MEMBER)
                { throw IllegalArgumentException("이미 요청한 역할을 갖고 있습니다.") }
            }
            ?.also { it.updateRoleToHost() }
            ?.let {
                jwtPlugin.generateAccessToken(
                    subject = it.id.toString(),
                    role = it.role.name
                )
            }
            ?.let { LoginResponse(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }

    fun getMemberReservationList(principal: UserPrincipal): List<ReservationResponse> {
        val today = LocalDate.now()
        return reservationRepository.findByMemberIdAndCheckOutGreaterThanEqual(principal.id, today)
            .map { ReservationResponse.from(it) }
    }

    fun getWishListedSpaceList(userPrincipal: UserPrincipal): List<WishListedSpaceResponse> {
        return spaceRepository.getWishListedSpaceList(userPrincipal.id)
            .map { WishListedSpaceResponse.fromEntity(it.key!!, it.value) }
    }

    fun getCouponList(userPrincipal: UserPrincipal): List<UserCouponResponse> {
        return userCouponRepository.getMemberCouponList(userPrincipal.id)
            .map { UserCouponResponse.from(it) }
    }
}