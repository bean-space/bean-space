package com.beanspace.beanspace.domain.space.repository

import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.model.QImage
import com.beanspace.beanspace.domain.member.model.QMember
import com.beanspace.beanspace.domain.space.model.QReview
import com.beanspace.beanspace.domain.space.model.Review
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ReviewQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ReviewQueryDslRepository {

    private val review = QReview.review
    private val member = QMember.member
    private val image = QImage.image

    override fun getLast3Reviews(spaceId: Long): List<Pair<Review?, List<String>>> {

        val result = queryFactory.select(
            review,
            image.imageUrl
        )
            .from(review)
            .join(review.member, member).fetchJoin()
            .leftJoin(image).on(image.contentId.eq(review.id).and(image.type.eq(ImageType.REVIEW)))
            .where(review.space.id.eq(spaceId))
            .orderBy(review.createdAt.desc(), image.orderIndex.asc())
            .limit(3)
            .fetch()

        return result.groupBy { it.get(review) }
            .map { Pair(it.key, it.value.map { v -> v.get(image.imageUrl) ?: "" }) }
    }
}