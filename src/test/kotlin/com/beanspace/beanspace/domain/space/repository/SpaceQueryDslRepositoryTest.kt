package com.beanspace.beanspace.domain.space.repository

import com.appmattus.kotlinfixture.config.range
import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.domain.member.model.Member
import com.beanspace.beanspace.domain.member.model.MemberRole
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.model.Reservation
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.Address
import com.beanspace.beanspace.domain.space.model.Space
import com.beanspace.beanspace.domain.space.model.SpaceStatus
import com.beanspace.beanspace.infra.querydsl.QueryDslConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.maps.shouldNotContainValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@ActiveProfiles("test")
class SpaceQueryDslRepositoryTest @Autowired constructor(
    private val spaceRepository: SpaceRepository,
    private val reservationRepository: ReservationRepository,
    private val memberRepository: MemberRepository,
) : BehaviorSpec({

    context("SpaceQueryDslRepository.search()") {

        beforeEach {
            reservationRepository.deleteAll()
            spaceRepository.deleteAll()
            memberRepository.deleteAll()
        }

        given("저장된 Space의 status가 각각 다를 때") {
            `when`("search()시") {
                then("space.status==SpaceStatus.ACTIVE인 Space만 반환된다.") {
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixtures =
                        generateSpaceFixtures(
                            numberOfFixtures = 50,
                            host = host,
                            status = fixture(listOf(SpaceStatus.ACTIVE, SpaceStatus.PENDING, SpaceStatus.REJECTED))
                        )
                    spaceRepository.saveAllAndFlush(spaceFixtures)

                    val (contents, totalCount) = spaceRepository.search(pageable = defaultPageable)

                    contents.forEach { it.key?.status shouldBe SpaceStatus.ACTIVE }
                    totalCount shouldBe spaceFixtures.filter { it.status == SpaceStatus.ACTIVE }.size
                }
            }
        }

        given("keyword로 search()시") {
            `when`("특정 키워드를 보내면") {
                then("Space.adress.sidoAndSigungu나 Space.listingName에 키워드를 포함한 Space만 반환된다.") {

                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixtures =
                        generateSpaceFixtures(numberOfFixtures = 50, host = host, status = SpaceStatus.ACTIVE)
                    spaceRepository.saveAllAndFlush(spaceFixtures)

                    val keyword = "경기 펜트하우스"

                    val (contents, totalCount) = spaceRepository.search(keyword = keyword, pageable = defaultPageable)

                    contents.size shouldBeLessThanOrEqual defaultPageable.pageSize
                    contents.forEach { space ->
                        keyword.split(" ").filter { it.isNotBlank() }.forEach { subKeyword ->
                            (space.key?.address?.sidoAndSigungu?.contains(subKeyword) ?: false ||
                                space.key?.listingName?.contains(subKeyword) ?: false) shouldBe true
                        }
                    }

                    totalCount shouldBe spaceFixtures.filter { space ->
                        keyword.split(" ").filter { it.isNotBlank() }.all { subKeyword ->
                            space.address.sidoAndSigungu.contains(subKeyword) ||
                                space.listingName.contains(subKeyword)
                        }
                    }.size
                }
            }
        }

        given("checkIn으로 search()시") {
            `when`("checkIn 날짜가 이미 reservation이 있는 날짜이면") {
                then("반환되는 Space가 없어야 한다.") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkIn = LocalDate.of(2024, 7, 1),
                        pageable = defaultPageable
                    )

                    contents.size shouldBe 0
                }
            }

            `when`("checkIn 날짜가 reservation이 없는 날짜이면") {
                then("반환되는 Space가 있어야 한다.") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkIn = LocalDate.of(2024, 7, 3),
                        pageable = defaultPageable
                    )
                    contents.size shouldNotBe 0
                }
            }
        }

        given("checkOut으로 search()시") {
            `when`("checkOut 날짜가 이미 reservation이 있는 날짜이면") {
                then("반환되는 Space가 없어야 한다.") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkOut = LocalDate.of(2024, 7, 3),
                        pageable = defaultPageable
                    )
                    contents.size shouldBe 0
                }
            }

            `when`("checkOut 날짜가 reservation이 없는 날짜이면") {
                then("반환되는 Space가 있어야 한다.") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkOut = LocalDate.of(2024, 7, 4),
                        pageable = defaultPageable
                    )
                    contents.size shouldNotBe 0
                }
            }
        }

        given("headCount로 search()시 ") {
            `when`("defaultPeople <= headCount <= maxPeople") {
                then("조건을 만족하는 Space만 반환된다.") {
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 50, host = host, status = SpaceStatus.ACTIVE)
                    spaceRepository.saveAllAndFlush(spaceFixture)

                    val headCount = 4

                    val (contents, totalCount) = spaceRepository.search(
                        headCount = headCount,
                        pageable = defaultPageable
                    )

                    contents.size shouldBeLessThanOrEqual defaultPageable.pageSize
                    contents.forEach {
                        it.key!!.defaultPeople shouldBeLessThanOrEqual headCount
                        it.key!!.maxPeople shouldBeGreaterThanOrEqual headCount
                    }

                    totalCount shouldBe spaceFixture.filter {
                        it.defaultPeople <= headCount && headCount <= it.maxPeople
                    }.size

                }
            }
        }

        given("checkIn과 CheckOut으로 search()시") {
            `when`("두 날짜의 범위에 reservation이 없으면") {
                then("반환되는 Space가 있어야 한다") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkIn = LocalDate.of(2024, 7, 3),
                        checkOut = LocalDate.of(2024, 7, 4),
                        pageable = defaultPageable
                    )

                    contents.size shouldNotBe 0
                }
            }

            `when`("두 날짜의 범위에 reservation이 있으면") {
                then("반환되는 Space가 없어야 한다") {
                    val guest = memberRepository.saveAndFlush(defaultGuest)
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 1, host = host, status = SpaceStatus.ACTIVE)
                    val space = spaceRepository.saveAndFlush(spaceFixture[0])

                    val baseDate = LocalDate.of(2024, 7, 1)

                    val reservations = getReservationFixtures(50, guest, space, baseDate)
                    reservationRepository.saveAllAndFlush(reservations)

                    val (contents, _) = spaceRepository.search(
                        checkIn = LocalDate.of(2024, 7, 1),
                        checkOut = LocalDate.of(2024, 7, 4),
                        pageable = defaultPageable
                    )

                    contents.size shouldBe 0
                }
            }
        }

        given("pageable외에 다른 조건이 없을 때") {
            `when`("pageSize를 특정 값으로 지정 후 serach()시") {
                then("pageSize만큼의 Space가 반환되고 totalCount는 space의 총 개수가 반환된다.") {

                    val totalSpaceCount = 50
                    val pageSize = 12

                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture = generateSpaceFixtures(
                        numberOfFixtures = totalSpaceCount,
                        host = host,
                        status = SpaceStatus.ACTIVE
                    )
                    spaceRepository.saveAllAndFlush(spaceFixture)

                    val pageable = PageRequest.of(0, pageSize)

                    val (contents, totalCount) = spaceRepository.search(pageable = pageable)

                    contents.size shouldBe pageSize
                    totalCount shouldBe totalSpaceCount
                }
            }

            `when`("pageCount가 다르면") {
                then("다른 Space 목록을 반환한다.") {
                    val host = memberRepository.saveAndFlush(defaultHost)

                    val spaceFixture =
                        generateSpaceFixtures(numberOfFixtures = 50, host = host, status = SpaceStatus.ACTIVE)
                    spaceRepository.saveAllAndFlush(spaceFixture)

                    val page1 = PageRequest.of(0, 12)
                    val page2 = PageRequest.of(1, 12)

                    val (contents1, _) = spaceRepository.search(pageable = page1)
                    val (contents2, _) = spaceRepository.search(pageable = page2)

                    contents1 shouldNotContainValue contents2
                }
            }
        }

    }

}) {
    companion object {
        val defaultPageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
        val fixture = kotlinFixture()

        private val costFixture = fixture<Long> {
            factory<Long> {
                range(100000L..10000000L)
            }
        }

        val defaultGuest = fixture<Member> {
            property(Member::role) { MemberRole.MEMBER }
        }

        val defaultHost = fixture<Member> {
            property(Member::role) { MemberRole.HOST }
        }

        fun generateSpaceFixtures(numberOfFixtures: Int, host: Member, status: SpaceStatus): List<Space> {
            return (1..numberOfFixtures).map {
                fixture<Space> {
                    property(Space::listingName) {
                        fixture(
                            listOf(
                                "경기에 있는 집",
                                "서울에 있는 집",
                                "고급 펜트하우스",
                                "펜션",
                                "아파트"
                            )
                        )
                    }
                    property(Address::sidoAndSigungu) {
                        fixture(
                            listOf(
                                "서울 구로구",
                                "경기 평택시",
                                "경기 의정부시",
                                "경기 시흥시",
                                "경기 파주시"
                            )
                        )
                    }
                    property(Space::defaultPeople) { fixture<Int> { factory<Int> { range(2..4) } } }
                    property(Space::maxPeople) { fixture<Int> { factory<Int> { range(4..8) } } }
                    property(Space::status) { status }
                    property(Space::host) { host }
                    property(Space::isDeleted) { false }
                }
            }
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
                    property(Reservation::cost) { costFixture }
                    property(Reservation::checkIn) { checkIn }
                    property(Reservation::checkOut) { checkOut }
                    property(Reservation::reservationPeople) { 2 }
                    property(Reservation::member) { guest }
                    property(Reservation::space) { space }
                }
            }
        }
    }
}
