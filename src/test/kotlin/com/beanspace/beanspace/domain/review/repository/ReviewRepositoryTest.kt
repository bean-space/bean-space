package com.beanspace.beanspace.domain.review.repository

import com.appmattus.kotlinfixture.config.range
import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.Address
import com.beanspace.beanspace.domain.space.model.Review
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.domain.space.repository.ReviewRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.infra.querydsl.QueryDslConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.random.Random

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@ActiveProfiles("test")
class ReviewRepositoryTest @Autowired constructor(
    private val reviewRepository: ReviewRepository,
    private val memberRepository: MemberRepository,
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
) : BehaviorSpec({

    context("ReviewRepository.getAverageRating()") {
        beforeEach {
            reviewRepository.deleteAll()
            reservationRepository.deleteAll()
            spaceRepository.deleteAll()
            memberRepository.deleteAll()
        }
        given("Space에 대한 Review가 여러개 있을 때") {
            `when`("getAverageRating() 실행하면") {
                then("rating의 평균값을 반환한다.") {
                    val host = memberRepository.saveAndFlush(defaultHost)
                    val guest = memberRepository.saveAndFlush(defaultGuest)

                    val spaceFixture = fixture<Space> {
                        property(Address::sido) { "경기도" }
                        property(Space::defaultPeople) { 2 }
                        property(Space::maxPeople) { 4 }
                        property(Space::status) { SpaceStatus.ACTIVE }
                        property(Space::host) { host }
                    }
                    val space = spaceRepository.saveAndFlush(spaceFixture)

                    val baseDate = LocalDate.of(2024, 3, 1)
                    val reservationFixtures = getReservationFixtures(5, guest, space, baseDate)
                    val reservationList = reservationRepository.saveAllAndFlush(reservationFixtures)

                    val reviewList =
                        reservationList.map {
                            Review(
                                content = "asdasd",
                                rating = Random.nextInt(1, 5),
                                member = guest,
                                reservation = it,
                                space = space,
                            )
                        }
                    reviewList.forEach { println(it.rating) }
                    reviewRepository.saveAllAndFlush(reviewList)

                    val result = reviewRepository.getAverageRating(space.id!!)

                    result shouldBe reviewList.map { it.rating }.average()

                }
            }
        }
    }
}) {
    companion object {
        val fixture = kotlinFixture()

        val defaultGuest = fixture<Member> {
            property(Member::role) { MemberRole.MEMBER }
        }

        val defaultHost = fixture<Member> {
            property(Member::role) { MemberRole.HOST }
        }

        fun getReservationFixtures(
            numberOfFixtures: Int,
            guest: Member,
            space: Space,
            baseDate: LocalDate
        ): List<Reservation> {
            return (1..numberOfFixtures).map { i ->

                val checkIn = baseDate.plusDays((i - 1) * 4L) // 매번 4일씩 뒤로 설정
                val checkOut = checkIn.plusDays(2) // checkIn 2일 뒤 체크아웃

                fixture<Reservation> {
                    property(Reservation::cost) { fixture<Long> { factory<Long> { range(100000L..10000000L) } } }
                    property(Reservation::checkIn) { checkIn }
                    property(Reservation::checkOut) { checkOut }
                    property(Reservation::reservationPeople) { 2 }
                    property(Reservation::member) { guest }
                    property(Reservation::space) { space }
                    property(Reservation::isCancelled) { false }
                }
            }
        }
    }
}