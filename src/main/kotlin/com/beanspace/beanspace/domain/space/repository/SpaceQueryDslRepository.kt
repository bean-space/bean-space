package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SpaceQueryDslRepository {
    fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Page<Space>
}
