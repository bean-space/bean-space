package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.AddReviewRequest
import com.beanspace.beanspace.api.space.dto.ReviewResponse
import com.beanspace.beanspace.api.space.dto.SpaceDetailResponse
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.api.space.dto.UpdateReviewRequest
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.model.Wishlist
import com.beanspace.beanspace.domain.space.repository.ReviewRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
import com.beanspace.beanspace.infra.security.dto.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SpaceService(
    private val spaceRepository: SpaceRepository,
    private val wishListRepository: WishListRepository,
    private val imageRepository: ImageRepository,
    private val reservationRepository: ReservationRepository,
    private val reviewRepository: ReviewRepository,
    private val memberRepository: MemberRepository,
) {
    fun getSpaceList(
        sido: String?,
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        headCount: Int?,
        pageable: Pageable
    ): Page<SpaceResponse> {
        val searchResult = spaceRepository.search(
            sido = sido,
            checkIn = checkIn,
            checkOut = checkOut,
            headCount = headCount,
            pageable = pageable
        )

        val (contents, totalCount) = searchResult

        if (contents.isEmpty() || totalCount == 0L) {
            return Page.empty()
        }
        val response = contents.map { SpaceResponse.from(it.key!!, it.value) }

        return PageImpl(response, pageable, totalCount)
    }

    fun getSpace(spaceId: Long): SpaceResponse {
        val space =
            spaceRepository.findByIdOrNull(spaceId) ?: throw ModelNotFoundException(model = "Space", id = spaceId)
        if (space.status != SpaceStatus.ACTIVE) throw ModelNotFoundException(model = "Space", id = spaceId)

        val imageList = imageRepository.findByTypeAndContentId(ImageType.SPACE, spaceId)

        val reservedDateList = mutableListOf<LocalDate>()
        // reservationRepository.findBySpaceAndCheckInGreaterThanEqualAndIsCancelledFalse( //TODO isCancelled == false 인 예약만가져오도록 수정하기
        //     space,
        //     LocalDate.now()
        // )
        //     ?.map { it.checkIn.datesUntil(it.checkOut).forEach { date -> reservedDateList.add(date) } }
        return SpaceResponse.from(space, imageList.map { it.imageUrl }, reservedDateList)
    }

    fun addToWishList(spaceId: Long, userPrincipal: UserPrincipal) {
        check(spaceRepository.existsByIdAndStatus(spaceId, SpaceStatus.ACTIVE))
        { throw ModelNotFoundException(model = "Space", id = spaceId) }

        check(!wishListRepository.existsBySpaceIdAndMemberId(spaceId, userPrincipal.id))
        { throw IllegalStateException("이미 찜한 공간입니다.") }

        wishListRepository.save(Wishlist(spaceId, userPrincipal.id))
    }

    @Transactional
    fun deleteFromWishList(spaceId: Long, userPrincipal: UserPrincipal) {

        check(wishListRepository.existsBySpaceIdAndMemberId(spaceId, userPrincipal.id))
        { throw IllegalStateException("해당 공간에 찜한 내역이 없습니다.") }

        wishListRepository.delete(Wishlist(spaceId, userPrincipal.id))
    }

    fun getReviews(spaceId: Long): List<ReviewResponse> {
        val reviewList = reviewRepository.findBySpaceId(spaceId)

        val imageUrlListMap = imageRepository.findAllByContentIdInAndTypeOrderByOrderIndexAsc(
            reviewList.map { it.id!! },
            contentType = ImageType.REVIEW
        ).groupBy { it.contentId }.mapValues { it.value.map { image -> image.imageUrl } }

        return reviewList.map { ReviewResponse.from(it, imageUrlListMap[it.id] ?: emptyList()) }
    }

    @Transactional
    fun addReview(spaceId: Long, request: AddReviewRequest, userPrincipal: UserPrincipal): Unit {
        val member = memberRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException(
            model = "Member",
            id = userPrincipal.id
        )
        val space =
            spaceRepository.findByIdOrNull(spaceId) ?: throw ModelNotFoundException(model = "Space", id = spaceId)
        val reservation = reservationRepository.findByIdOrNull(request.reservationId)
            ?.also { check(it.validateOwner(userPrincipal.id)) { throw NoPermissionException() } }
            ?.also { check(it.space.id == spaceId) { throw IllegalArgumentException("해당 공간에 대한 예약이 아닙니다.") } }
            ?.also { check(it.isReviewAllowed()) { throw IllegalStateException("아직 후기를 작성할 수 없습니다. (체크아웃 당일 12:00 부터 작성 가능)") } }
            ?: throw ModelNotFoundException(model = "Reservation", id = request.reservationId)

        if (reviewRepository.existsByReservation(reservation)) throw IllegalStateException("해당 예약에 대한 후기를 이미 남겼습니다.")

        reviewRepository.save(request.toEntity(member, space, reservation))
            .also {
                request.imageUrlList.forEachIndexed { index, imageUrl ->
                    imageRepository.save(
                        Image(
                            type = ImageType.REVIEW,
                            contentId = it.id!!,
                            imageUrl = imageUrl,
                            orderIndex = index
                        )
                    )
                }
            }
    }

    @Transactional
    fun updateReview(spaceId: Long, reviewId: Long, request: UpdateReviewRequest, userPrincipal: UserPrincipal) {
        val member = memberRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException(
            model = "Member",
            id = userPrincipal.id
        )
        val space =
            spaceRepository.findByIdOrNull(spaceId) ?: throw ModelNotFoundException(model = "Space", id = spaceId)

        val review = reviewRepository.findByIdOrNull(reviewId)
            ?.also { check(it.space.id == spaceId) { throw IllegalArgumentException("해당 공간에 대한 후기가 아닙니다.") } }
            ?.also { check(it.member.id == userPrincipal.id) { throw NoPermissionException() } }
            ?.also { it.update(request.content, request.rating) }
            ?.also { imageRepository.deleteByTypeAndContentId(ImageType.REVIEW, reviewId) }
            ?.also {
                request.imageUrlList.forEachIndexed { index, imageUrl ->
                    imageRepository.save(
                        Image(
                            type = ImageType.REVIEW,
                            contentId = reviewId,
                            imageUrl = imageUrl,
                            orderIndex = index
                        )
                    )
                }
            }
    }

    @Transactional
    fun deleteReview(spaceId: Long, reviewId: Long, userPrincipal: UserPrincipal) {
        val review = reviewRepository.findByIdOrNull(reviewId)
            ?.also { check(it.space.id == spaceId) { throw IllegalArgumentException("해당 공간에 대한 예약이 아닙니다.") } }
            ?.also { check(it.member.id == userPrincipal.id) { throw NoPermissionException() } }
            ?: throw ModelNotFoundException(model = "Review", id = reviewId)

        reviewRepository.delete(review)
            .also { imageRepository.deleteByTypeAndContentId(ImageType.REVIEW, reviewId) }
    }
}