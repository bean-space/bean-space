package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.model.Wishlist
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

    fun addReview(spaceId: Long): Unit {
        TODO()
    }
}