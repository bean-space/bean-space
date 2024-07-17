package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.space.model.QSpace
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SpaceQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : SpaceQueryDslRepository {

    private val space = QSpace.space

    override fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Page<Space> {
        val whereClause = BooleanBuilder()
        whereClause.and(space.status.eq(spaceStatus))

        val totalCount = queryFactory.select(space.count())
            .from(space)
            .where(whereClause)
            .fetchOne() ?: 0L

        val contents = queryFactory.selectFrom(space)
            .where(whereClause)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, space))
            .fetch()

        return PageImpl(contents, pageable, totalCount)
    }

    private fun getOrderSpecifier(pageable: Pageable, path: EntityPathBase<*>): Array<OrderSpecifier<*>> {
        val pathBuilder = PathBuilder(path.type, path.metadata)

        return pageable.sort.toList().map { order ->
            OrderSpecifier(
                if (order.isAscending) Order.ASC else Order.DESC,
                pathBuilder.get(order.property) as Expression<Comparable<*>>
            )
        }.toTypedArray()
    }
}