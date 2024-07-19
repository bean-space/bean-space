package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.querydsl.core.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface SpaceQueryDslRepository {
    fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Page<Space>
    fun search(
        sido: String?,
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        headCount: Int?,
        pageable: Pageable
    ): Pair<Map<Space?, List<String>>, Long>

    fun getReservedDateList(spaceId: Long): MutableList<Tuple>?
}
