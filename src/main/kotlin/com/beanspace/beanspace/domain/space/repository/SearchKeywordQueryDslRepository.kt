package com.beanspace.beanspace.domain.space.repository

import java.time.LocalDateTime

interface SearchKeywordQueryDslRepository {
    fun getPopularKeywords(from: LocalDateTime, to: LocalDateTime): List<String>
}