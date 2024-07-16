package com.beanspace.beanspace.domain.space.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SpaceQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : SpaceQueryDslRepository