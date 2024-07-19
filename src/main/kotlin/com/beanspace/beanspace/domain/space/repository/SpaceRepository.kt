package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import org.springframework.data.jpa.repository.JpaRepository

interface SpaceRepository : JpaRepository<Space, Long>, SpaceQueryDslRepository {
    fun findByIdAndStatus(id: Long, status: SpaceStatus): Space?
    fun existsByIdAndStatus(id: Long, status: SpaceStatus): Boolean
}
