package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.model.QImage
import com.beanspace.beanspace.domain.reservation.model.QReservation
import com.beanspace.beanspace.domain.space.model.QSpace
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.core.util.StringUtils
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class SpaceQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : SpaceQueryDslRepository {

    private val space = QSpace.space
    private val reservation = QReservation.reservation
    private val image = QImage.image

    override fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Page<Space> {
        val totalCount = queryFactory.select(space.count())
            .from(space)
            .where(eqStatus(spaceStatus))
            .fetchOne() ?: 0L

        val contents = queryFactory.selectFrom(space)
            .where(eqStatus(spaceStatus))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, space))
            .fetch()

        return PageImpl(contents, pageable, totalCount)
    }

    override fun search(
        sido: String?,
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        headCount: Int?,
        pageable: Pageable
    ): Pair<Map<Space?, List<String>>, Long> {

        val conditions = BooleanBuilder()
            .and(eqStatus(SpaceStatus.ACTIVE))
            .and(eqSido(sido))
            .and(inAvailableDate(checkIn, checkOut))
            .and(isAvailableHeadCount(headCount))

        val totalCount = queryFactory.select(space.count())
            .from(space)
            .where(conditions)
            .fetchOne() ?: return Pair(emptyMap(), 0)

        val paginatedSpaceId = queryFactory
            .select(space.id).distinct()
            .from(space)
            .where(conditions)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, space))
            .fetch()

        val result = queryFactory.select(space, image.imageUrl)
            .from(space)
            .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
            .where(space.id.`in`(paginatedSpaceId))
            .orderBy(*getOrderSpecifier(pageable, space))
            .fetch()

        val contents = result.groupBy { it.get(QSpace.space) }
            .mapKeys { (space, _) -> space }
            .mapValues { it.value.map { tuple -> tuple.get(QImage.image.imageUrl) ?: "" } }

        return Pair(contents, totalCount)
    }

    override fun getReservedDateList(spaceId: Long): MutableList<Tuple>? {
        return queryFactory.select(reservation.checkIn, reservation.checkOut)
            .from(reservation)
            .join(space).on(reservation.space.id.eq(spaceId))
            .fetch()
    }

    private fun isAvailableHeadCount(headCount: Int?): BooleanExpression? {
        return if (headCount != null && headCount > 0) space.maxPeople.goe(headCount) else null
    }

    private fun inAvailableDate(start: LocalDate?, end: LocalDate?): BooleanExpression? {
        val startCondition =
            if (start != null) reservation.checkIn.loe(start).and(reservation.checkOut.gt(start)) else null
        val endCondition = if (end != null) reservation.checkIn.lt(end).and(reservation.checkOut.goe(end)) else null

        val unavailableSpaceIds = JPAExpressions.select(reservation.space.id)
            .from(reservation)
            .where(BooleanBuilder().and(startCondition).or(endCondition))
        return space.id.notIn(unavailableSpaceIds)
    }

    private fun eqStatus(spaceStatus: SpaceStatus?): BooleanExpression? {
        return if (spaceStatus != null) space.status.eq(spaceStatus) else null
    }

    private fun eqSido(sido: String?): BooleanExpression? {
        return if (!StringUtils.isNullOrEmpty(sido)) space.address.sido.eq(sido) else null
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