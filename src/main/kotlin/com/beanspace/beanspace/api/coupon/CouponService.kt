package com.beanspace.beanspace.api.coupon

import com.beanspace.beanspace.api.coupon.dto.CouponResponse
import com.beanspace.beanspace.domain.coupon.model.UserCoupon
import com.beanspace.beanspace.domain.coupon.repository.CouponRepository
import com.beanspace.beanspace.domain.coupon.repository.UserCouponRepository
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.TimeUnit

@Service
class CouponService(
    private val userCouponRepository: UserCouponRepository,
    private val memberRepository: MemberRepository,
    private val couponRepository: CouponRepository,
    private val redissonClient: RedissonClient,
    private val transactionTemplate: TransactionTemplate
) {

    private val logger = LoggerFactory.getLogger(CouponService::class.java)

    fun getCouponList(): List<CouponResponse> {
        return couponRepository.findAccessibleCoupons()
            .map { CouponResponse.from(it) }
    }

    fun issueCoupon(memberId: Long, couponId: Long) {
        val key = "coupon:$couponId"
        val lock = redissonClient.getLock(key)

        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) throw IllegalStateException("쿠폰 발행을 다시 시도해주세요")

            transactionTemplate.execute {
                val member = memberRepository.findByIdOrNull(memberId)
                    ?: throw ModelNotFoundException("멤버", memberId)

                val coupon = couponRepository.findByIdOrNull(couponId)
                    ?: throw ModelNotFoundException("쿠폰", couponId)

                check(coupon.isCouponStockAvailable()) { throw IllegalStateException("쿠폰 발급이 마감 되었습니다.") }

                check(coupon.isIssuePeriodValid()) { throw IllegalStateException("쿠폰 발급 가능 시간을 확인해주세요.") }

                check(!userCouponRepository.existsByCouponIdAndMemberId(couponId, memberId))
                { throw IllegalStateException("이미 발급 받은 쿠폰입니다.") }

                coupon.issueCoupon()

                UserCoupon(member, coupon)
                    .let { userCouponRepository.save(it) }
            }
        } catch (e: Exception) {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
            logger.error(e.message, e)
            throw e
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}