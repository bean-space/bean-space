package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.Review

interface ReviewQueryDslRepository {
    fun getLast3Reviews(spaceId: Long): List<Pair<Review?, List<String>>>
}