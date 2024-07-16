package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Space
import org.springframework.data.jpa.repository.JpaRepository

interface SpaceRepository : JpaRepository<Space, Long>, SpaceQueryDslRepository
