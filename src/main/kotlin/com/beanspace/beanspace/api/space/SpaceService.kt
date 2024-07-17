package com.beanspace.beanspace.api.space

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.space.model.Wishlist
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpaceService(
    private val spaceRepository: SpaceRepository,
    private val wishListRepository: WishListRepository
) {
    fun getSpaceList(): List<SpaceResponse> {
        //TODO 이미지 가져와야함
        return spaceRepository.findAll().map { SpaceResponse.from(it) }
    }

    fun getSpace(spaceId: Long): SpaceResponse {
        val space = spaceRepository.findByIdOrNull(spaceId) ?: throw Exception() //TODO Custom Exception 적용
        //TODO 이미지 가져와야함
        return SpaceResponse.from(space)
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