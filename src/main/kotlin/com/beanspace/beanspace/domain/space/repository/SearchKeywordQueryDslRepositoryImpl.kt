package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.QSearchKeyword
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SearchKeywordQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : SearchKeywordQueryDslRepository {

    private val searchKeyword = QSearchKeyword.searchKeyword

    override fun getPopularKeywords(from: LocalDateTime, to: LocalDateTime): List<String> {
        return queryFactory
            .select(searchKeyword.keyword)
            .from(searchKeyword)
            .where(searchKeyword.createdAt.between(from, to))
            .groupBy(searchKeyword.keyword)
            .orderBy(searchKeyword.keyword.count().desc())
            .limit(10)
            .fetch()
    }
}
