package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface SpaceQueryDslRepository {
    fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Pair<Map<Space?, List<String>>, Long>
    fun search(
        sido: String? = null,
        checkIn: LocalDate? = null,
        checkOut: LocalDate? = null,
        headCount: Int? = null,
        pageable: Pageable
    ): Pair<Map<Space?, List<String>>, Long>

    fun getWishListedSpaceList(memberId: Long): Map<Space?, List<String>>
}
