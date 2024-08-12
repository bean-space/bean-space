package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.model.QImage
import com.beanspace.beanspace.domain.reservation.model.QReservation
import com.beanspace.beanspace.domain.space.model.QReview
import com.beanspace.beanspace.domain.space.model.QSpace
import com.beanspace.beanspace.domain.space.model.QSpaceOffer
import com.beanspace.beanspace.domain.space.model.QWishlist
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class SpaceQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : SpaceQueryDslRepository {

    private val space = QSpace.space
    private val reservation = QReservation.reservation
    private val image = QImage.image
    private val wishlist = QWishlist.wishlist
    private val spaceOffer = QSpaceOffer.spaceOffer
    private val review = QReview.review

    override fun findByStatus(pageable: Pageable, spaceStatus: SpaceStatus): Pair<Map<Space?, List<String>>, Long> {
        val conditions = BooleanBuilder()
            .and(eqStatus(spaceStatus))
            .and(space.isDeleted.eq(false))

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
            .orderBy(getOrderSpecifier(pageable, space))
            .fetch()

        val result = queryFactory.select(space, image.imageUrl)
            .from(space)
            .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
            .where(space.id.`in`(paginatedSpaceId))
            .orderBy(getOrderSpecifier(pageable, space), image.orderIndex.asc())
            .fetch()

        val contents = result.groupBy { it.get(QSpace.space) }
            .mapKeys { (space, _) -> space }
            .mapValues { it.value.map { tuple -> tuple.get(QImage.image.imageUrl) ?: "" } }

        return Pair(contents, totalCount)
    }

    override fun search(
        keyword: String?,
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        headCount: Int?,
        priceMin: Int?,
        priceMax: Int?,
        bedRoomCount: Int?,
        bedCount: Int?,
        bathRoomCount: Int?,
        offer: List<Long>?,
        pageable: Pageable
    ): Pair<Map<Space?, Pair<List<String>, Double?>>, Long> {

        val conditions = BooleanBuilder()
            .and(eqStatus(SpaceStatus.ACTIVE))
            .and(isContainsKeyword(keyword))
            .and(inAvailableDate(checkIn, checkOut))
            .and(isAvailableHeadCount(headCount))
            .and(isGreaterOrEqualThanMinPrice(priceMin))
            .and(isLowerOrEqualThanMaxPrice(priceMax))
            .and(isGreaterOrEqualThanBedRoomCount(bedRoomCount))
            .and(isGreaterOrEqualThanBedCount(bedCount))
            .and(isGreaterOrEqualThanBathRoomCount(bathRoomCount))
            .and(hasAllOffer(offer))
            .and(space.isDeleted.eq(false))

        val totalCount = queryFactory.select(space.count())
            .from(space)
            .where(conditions)
            .fetchOne() ?: return Pair(emptyMap(), 0)

        val sortProperty = pageable.sort.toList().map { it.property }[0]

        val rating = Expressions.numberPath(Double::class.java, "rating")

        val result: List<Tuple> = when (sortProperty) {
            "popular" -> {
                val count = Expressions.numberPath(Long::class.java, "count")

                val paginatedSpaceId = queryFactory
                    .select(space.id, reservation.id.count().`as`(count)).distinct()
                    .from(space)
                    .leftJoin(reservation)
                    .on(
                        reservation.space.id.eq(space.id)
                            .and(reservation.createdAt.goe(LocalDateTime.now().minusDays(7)))
                    )
                    .where(conditions)
                    .groupBy(space.id)
                    .orderBy(count.desc())
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .fetch()
                    .map { it.get(space.id) }

                queryFactory.select(
                    space,
                    image.imageUrl,
                    reservation.id.count().`as`(count),
                    review.rating.avg().`as`(rating)
                )
                    .from(space)
                    .leftJoin(reservation).on(reservation.space.id.eq(space.id))
                    .leftJoin(review).on(review.space.id.eq(space.id))
                    .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
                    .where(space.id.`in`(paginatedSpaceId))
                    .groupBy(space.id, image.imageUrl)
                    .orderBy(count.desc(), image.orderIndex.asc())
                    .fetch()
            }

            "rating" -> {
                val paginatedSpaceId = queryFactory
                    .select(space.id, review.rating.avg().`as`(rating)).distinct()
                    .from(space)
                    .leftJoin(review).on(review.space.id.eq(space.id))
                    .where(conditions)
                    .groupBy(space.id)
                    .orderBy(rating.desc())
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .fetch()
                    .map { it.get(space.id) }

                queryFactory.select(space, image.imageUrl, review.rating.avg().`as`(rating))
                    .from(space)
                    .leftJoin(review).on(review.space.id.eq(space.id))
                    .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
                    .where(space.id.`in`(paginatedSpaceId))
                    .groupBy(space.id, image.imageUrl)
                    .orderBy(rating.desc(), image.orderIndex.asc())
                    .fetch()
            }

            else -> {
                val paginatedSpaceId = queryFactory
                    .select(space.id).distinct()
                    .from(space)
                    .where(conditions)
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .orderBy(getOrderSpecifier(pageable, space))
                    .fetch()

                queryFactory.select(space, image.imageUrl, review.rating.avg().`as`(rating))
                    .from(space)
                    .leftJoin(review).on(review.space.id.eq(space.id))
                    .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
                    .where(space.id.`in`(paginatedSpaceId))
                    .groupBy(space.id, image.imageUrl)
                    .orderBy(getOrderSpecifier(pageable, space), image.orderIndex.asc())
                    .fetch()
            }
        }

        val contents = result.groupBy { it.get(QSpace.space) }
            .mapKeys { (space, _) -> space }
            .mapValues { (_, tuples) ->
                Pair(
                    tuples.map { tuple -> tuple.get(QImage.image.imageUrl) ?: "" },
                    tuples.firstOrNull()?.get(rating)
                )
            }

        return Pair(contents, totalCount)
    }

    override fun getWishListedSpaceList(memberId: Long): Map<Space?, List<String>> {

        val wishListedSpaceIds = queryFactory
            .select(wishlist.spaceId)
            .from(wishlist)
            .where(wishlist.memberId.eq(memberId))
            .fetch()

        val result = queryFactory.select(space, image.imageUrl)
            .from(space)
            .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
            .where(space.id.`in`(wishListedSpaceIds))
            .fetch()

        val contents = result.groupBy { it.get(QSpace.space) }
            .mapKeys { (space, _) -> space }
            .mapValues { it.value.map { tuple -> tuple.get(QImage.image.imageUrl) ?: "" } }

        return contents
    }

    override fun getMostPopular4SpaceList(): Map<Space?, List<String>> {

        val oneWeekAgo = LocalDateTime.now().minusWeeks(1)

        val mostPopular4spaceIds = queryFactory
            .select(reservation.space.id)
            .from(reservation)
            .where(
                reservation.createdAt.after(oneWeekAgo)
                    .and(reservation.isCancelled.eq(false))
            )
            .groupBy(reservation.space.id)
            .orderBy(reservation.space.id.count().desc())
            .limit(4)
            .fetch()

        val result = queryFactory.select(space, image.imageUrl)
            .from(space)
            .leftJoin(image).on(image.contentId.eq(space.id).and(image.type.eq(ImageType.SPACE)))
            .where(space.id.`in`(mostPopular4spaceIds))
            .fetch()

        val contents = result.groupBy { it.get(QSpace.space) }
            .mapKeys { (space, _) -> space }
            .mapValues { it.value.map { tuple -> tuple.get(QImage.image.imageUrl) ?: "" } }

        return contents
    }

    private fun isContainsKeyword(keyword: String?): BooleanExpression? {
        return keyword?.let { fullKeyword ->
            val keywords = fullKeyword.split(" ").filter { it.isNotBlank() }

            keywords.map {
                space.listingName.containsIgnoreCase(it)
                    .or(space.address.sidoAndSigungu.contains(it))
            }.reduce { a, b -> a.and(b) }
        }
    }

    private fun isAvailableHeadCount(headCount: Int?): BooleanExpression? {
        return if (headCount != null && headCount > 0) space.maxPeople.goe(headCount) else null
    }

    private fun isGreaterOrEqualThanMinPrice(priceMin: Int?): BooleanExpression? {
        return if (priceMin != null && priceMin > 0) space.price.goe(priceMin) else null
    }

    private fun isLowerOrEqualThanMaxPrice(priceMax: Int?): BooleanExpression? {
        return if (priceMax != null && priceMax > 0) space.price.loe(priceMax) else null
    }

    private fun isGreaterOrEqualThanBedRoomCount(bedRoomCount: Int?): BooleanExpression? {
        return if (bedRoomCount != null) space.bedRoomCount.goe(bedRoomCount) else null
    }

    private fun isGreaterOrEqualThanBedCount(bedCount: Int?): BooleanExpression? {
        return if (bedCount != null) space.bedCount.goe(bedCount) else null
    }

    private fun isGreaterOrEqualThanBathRoomCount(bathRoomCount: Int?): BooleanExpression? {
        return if (bathRoomCount != null) space.bathRoomCount.goe(bathRoomCount) else null
    }

    private fun hasAllOffer(offer: List<Long>?): BooleanExpression? {
        return if (!offer.isNullOrEmpty()) {
            val offerCount = offer.size.toLong()
            space.id.`in`(
                JPAExpressions
                    .select(spaceOffer.space.id)
                    .from(spaceOffer)
                    .where(spaceOffer.offer.id.`in`(offer))
                    .groupBy(spaceOffer.space.id)
                    .having(spaceOffer.offer.id.countDistinct().eq(offerCount))
            )
        } else {
            null
        }
    }

    private fun inAvailableDate(start: LocalDate?, end: LocalDate?): BooleanExpression {
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

    private fun getOrderSpecifier(pageable: Pageable, path: EntityPathBase<*>): OrderSpecifier<*> {
        val pathBuilder = PathBuilder(path.type, path.metadata)

        return pageable.sort.toList().map { order ->
            OrderSpecifier(
                if (order.isAscending) Order.ASC else Order.DESC,
                pathBuilder.get(order.property) as Expression<Comparable<*>>
            )
        }.toTypedArray()[0]
    }
}
