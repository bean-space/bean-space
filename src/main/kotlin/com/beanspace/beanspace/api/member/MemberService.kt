package com.beanspace.beanspace.api.member

import com.beanspace.beanspace.api.auth.dto.LoginResponse
import com.beanspace.beanspace.api.coupon.dto.UserCouponResponse
import com.beanspace.beanspace.api.member.dto.MemberProfileResponse
import com.beanspace.beanspace.api.member.dto.MemberReservationResponse
import com.beanspace.beanspace.api.member.dto.UpdateProfileRequest
import com.beanspace.beanspace.api.member.dto.UpdateSocialUserInfoRequest
import com.beanspace.beanspace.api.space.dto.WishListedSpaceResponse
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.repository.ReviewRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import com.beanspace.beanspace.infra.security.jwt.JwtPlugin
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val userCouponRepository: UserCouponRepository,
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val reviewRepository: ReviewRepository,
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

    fun getMemberReservationList(principal: UserPrincipal): List<MemberReservationResponse> {
        val today = LocalDate.now()
        val oneYearAgo = today.minusYears(1)

        val reservations = reservationRepository.findByMemberIdAndCheckOutAfter(principal.id, oneYearAgo)
        val reservationIds = reservations.map { it.id!! }
        val reservationIdsWithReviews = reviewRepository.findByReservationIdIn(reservationIds)
            .map { it.reservation.id!! }

        return reservations.map {
            val isReviewed = reservationIdsWithReviews.contains(it.id!!)
            MemberReservationResponse.from(it, isReviewed)
        }
    }

    fun getWishListedSpaceList(userPrincipal: UserPrincipal): List<WishListedSpaceResponse> {
        return spaceRepository.getWishListedSpaceList(userPrincipal.id)
            .map { WishListedSpaceResponse.fromEntity(it.key!!, it.value) }
    }

    fun getCouponList(userPrincipal: UserPrincipal): List<UserCouponResponse> {
        return userCouponRepository.getMemberCouponList(userPrincipal.id)
            .map { UserCouponResponse.from(it) }
    }

    @Transactional
    fun updateSocialUserInfo(principal: UserPrincipal, request: UpdateSocialUserInfoRequest): MemberProfileResponse {
        return memberRepository.findByIdOrNull(principal.id)
            ?.also { check(it.isSocialUser()) { throw AccessDeniedException("소셜 유저가 아닙니다!") } }
            ?.also { check(it.isPhoneNumberEmpty()) { throw IllegalStateException("이미 전화번호가 있습니다") } }
            ?.also { it.updateSocialUserInfo(request.phoneNumber, request.email) }
            ?.let { MemberProfileResponse.fromEntity(it) }
            ?: throw ModelNotFoundException("Member", principal.id)
    }
}