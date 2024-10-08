package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Wishlist
import org.springframework.data.jpa.repository.JpaRepository

interface WishListRepository : JpaRepository<Wishlist, Long> {
    fun existsBySpaceIdAndMemberId(spaceId: Long, memberId: Long): Boolean
    fun deleteAllBySpaceId(spaceId: Long)
}