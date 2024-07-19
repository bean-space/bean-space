package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.model.Wishlist
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
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
    fun getSpaceList(): List<SpaceResponse> {
        //TODO 이미지 가져와야함
        return spaceRepository.findAll().map { SpaceResponse.from(it) }
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

    fun addToWishList(spaceId: Long /*인증정보*/) {
        // spaceId 존재 검증
        val memberId = 1L
        // 사용자의 Id로 찜을 누른적이 있는지 검증
        wishListRepository.save(Wishlist(spaceId, memberId))
    }

    @Transactional
    fun deleteFromWishList(spaceId: Long) {
        // spaceId 존재 검증
        val memberId = 1L
        // 사용자의 Id로 찜을 누른적이 있는지 확인
        wishListRepository.delete(Wishlist(spaceId, memberId))
    }

    fun addReview(spaceId: Long): Unit {
        TODO()
    }
}