package com.beanspace.beanspace.api.space

import com.appmattus.kotlinfixture.config.range
import com.appmattus.kotlinfixture.kotlinFixture
import com.beanspace.beanspace.api.space.dto.AddReviewRequest
import com.beanspace.beanspace.api.space.dto.SpaceDetailResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
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
import com.beanspace.beanspace.domain.space.repository.SearchKeywordRepository
import com.beanspace.beanspace.domain.space.repository.SpaceOfferRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class SpaceServiceTest : BehaviorSpec({
    val spaceRepository: SpaceRepository = mockk(relaxed = true)
    val wishListRepository: WishListRepository = mockk(relaxed = true)
    val imageRepository: ImageRepository = mockk(relaxed = true)
    val reservationRepository: ReservationRepository = mockk(relaxed = true)
    val reviewRepository: ReviewRepository = mockk(relaxed = true)
    val memberRepository: MemberRepository = mockk(relaxed = true)
    val searchKeywordRepository: SearchKeywordRepository = mockk(relaxed = true)
    val spaceOfferRepository: SpaceOfferRepository = mockk(relaxed = true)

    val spaceService = SpaceService(
        spaceRepository = spaceRepository,
        wishListRepository = wishListRepository,
        imageRepository = imageRepository,
        reservationRepository = reservationRepository,
        reviewRepository = reviewRepository,
        memberRepository = memberRepository,
        searchKeywordRepository = searchKeywordRepository,
        spaceOfferRepository = spaceOfferRepository
    )

    afterContainer { clearAllMocks() }

    context("SpaceService.getSpace()") {
        given("Space의 Id로 ") {
            `when`("존재하지 않는 Space를 조회하면") {
                then("ModelNotFoundException이 발생한다") {
                    every { spaceRepository.findByIdOrNull(1L) } returns null
                    shouldThrow<ModelNotFoundException> { spaceService.getSpace(1L, LocalDate.of(2024, 7, 1)) }
                }
            }

            `when`("ACTIVE 상태가 아닌 Space를 조회하면") {
                then("ModelNotFoundException이 발생한다") {
                    val today = LocalDate.of(2024, 7, 1)

                    val pendingSpace = mockk<Space> {
                        every { id } returns 1L
                        every { status } returns SpaceStatus.PENDING
                    }

                    val rejectedSpace = mockk<Space> {
                        every { id } returns 2L
                        every { status } returns SpaceStatus.REJECTED
                    }

                    every { spaceRepository.findByIdOrNull(1L) } returns pendingSpace
                    every { spaceRepository.findByIdOrNull(2L) } returns rejectedSpace

                    shouldThrow<ModelNotFoundException> { spaceService.getSpace(1L, today) }
                    shouldThrow<ModelNotFoundException> { spaceService.getSpace(2L, today) }
                }
            }

            `when`("getSpace() 실행하면") {
                then("해당 Space에 대해 오늘 이후로 예약된 날짜들이 reservedDateList에 담겨 반환된다. ") {
                    val space = getSpaceFixtures(1, defaultHost, SpaceStatus.ACTIVE)[0]
                    val imageList = getImageFixtures(5, ImageType.SPACE)
                    val reviewList = getReviewFixtures(3, space)

                    every { spaceRepository.findByIdOrNull(1L) } returns space
                    every {
                        imageRepository.findAllByContentIdAndTypeOrderByOrderIndexAsc(
                            1L,
                            ImageType.SPACE
                        )
                    } returns imageList

                    val today = LocalDate.of(2024, 7, 10)

                    every {
                        reservationRepository.findAllBySpaceIdAndIsCancelledAndCheckOutAfter(
                            space.id!!,
                            false,
                            today,
                        )
                    } returns listOf(
                        mockk<Reservation> {
                            every { checkIn } returns LocalDate.of(2024, 7, 8)
                            every { checkOut } returns LocalDate.of(2024, 7, 13)
                        },
                        mockk<Reservation> {
                            every { checkIn } returns LocalDate.of(2024, 7, 15)
                            every { checkOut } returns LocalDate.of(2024, 7, 16)
                        },
                        mockk<Reservation> {
                            every { checkIn } returns LocalDate.of(2024, 7, 16)
                            every { checkOut } returns LocalDate.of(2024, 7, 18)
                        },
                    )

                    val result = spaceService.getSpace(1L, today)

                    result.shouldBeTypeOf<SpaceDetailResponse>()
                    result.reservedDateList shouldBeEqual listOf(
                        LocalDate.of(2024, 7, 11),
                        LocalDate.of(2024, 7, 12),
                        LocalDate.of(2024, 7, 15),
                        LocalDate.of(2024, 7, 16),
                        LocalDate.of(2024, 7, 17),
                    )
                }
            }
        }
    }

    context("SpaceService.addReview()") {

        val request = AddReviewRequest(
            content = "so good place",
            rating = 5,
            reservationId = 1L,
            imageUrlList = listOf("some-img-url", "other-image-url")
        )

        val spaceFixture = getSpaceFixtures(1, defaultHost, SpaceStatus.ACTIVE)[0]

        given("존재하지 않는 Space에 대해") {
            `when`("addReview() 실행하면") {
                then("ModelNotFoundException이 발생한다.") {

                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns null

                    shouldThrow<ModelNotFoundException> {
                        spaceService.addReview(spaceFixture.id!!, request, 1L)
                    }
                }
            }
        }

        given("존재하지 않는 Reservation에 대해") {
            `when`("addReview() 실행하면") {
                then("ModelNotFoundException이 발생한다.") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns null

                    shouldThrow<ModelNotFoundException> {
                        spaceService.addReview(spaceFixture.id!!, request, 1L)
                    }
                }
            }
        }

        given("예약 당사자가 아닌 회원이 해당 예약에 대해서") {
            `when`("addReview() 실행하면") {
                then("NoPermissionException이 발생한다.") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns false
                    }

                    shouldThrow<NoPermissionException> {
                        spaceService.addReview(spaceFixture.id!!, request, 1L)
                    }
                }
            }
        }

        given("해당 공간에 대한 예약이 아닐시") {
            `when`("addReview() 실행하면") {
                then("IllegalArgumentException이 발생한다.") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { space } returns spaceFixture //spaceFixture.id = 1L
                    }

                    shouldThrow<IllegalArgumentException> {
                        spaceService.addReview(2L, request, 1L)
                    }
                }
            }
        }

        given("현재 시간이 예약 체크아웃 당일 12:00 전일 떄 ") {
            `when`("addReview() 실행하면") {
                then("IllegalStateException이 발생한다.") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { space } returns spaceFixture //spaceFixture.id = 1L
                        every { isReviewAllowed(any()) } returns false
                    }

                    shouldThrow<IllegalStateException> {
                        spaceService.addReview(1L, request, 1L)
                    }
                }
            }
        }

        given("이미 해당 예약에 대한 후기가 있는 상태에서") {
            `when`("addReview() 실행하면") {
                then("IllegalStateException이 발생한다.") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { space } returns spaceFixture //spaceFixture.id = 1L
                        every { isReviewAllowed(any()) } returns true
                    }
                    every { reviewRepository.existsByReservation(any()) } returns true

                    shouldThrow<IllegalStateException> {
                        spaceService.addReview(1L, request, 1L)
                    }
                }
            }
        }

        given("후기 작성 조건을 모두 만족한 상태에서") {
            `when`("addReview() 실행하면") {
                then("후기가 저장된다") {
                    every { memberRepository.findByIdOrNull(any()) } returns defaultGuest
                    every { spaceRepository.findByIdOrNull(any()) } returns spaceFixture
                    every { reservationRepository.findByIdOrNull(any()) } returns mockk<Reservation> {
                        every { validateOwner(1L) } returns true
                        every { space } returns spaceFixture //spaceFixture.id = 1L
                        every { isReviewAllowed(any()) } returns true
                    }
                    every { reviewRepository.existsByReservation(any()) } returns false
                    every { reviewRepository.save(any()) } returns mockk<Review> {
                        every { id } returns 1L
                    }
                    every { imageRepository.save(any()) } returns mockk<Image>()

                    spaceService.addReview(1L, request, 1L)

                    verify { reviewRepository.save(any()) }
                    verify(exactly = request.imageUrlList.size) { imageRepository.save(any()) }
                }
            }
        }
    }

}) {
    companion object {
        val defaultPageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
        val fixture = kotlinFixture()

        private val costFixture = fixture<Long> {
            filter<Long> {
                filter { it < 10000000 }
            }
        }

        private val minFixture = fixture<Int> {
            factory<Int> {
                range(2..4)
            }
        }

        private val maxFixture = fixture<Int> {
            factory<Int> {
                range(4..8)
            }
        }

        private val textFixture = fixture<String> {
            filter<String> {
                filter { it.length < 100 }
            }
        }

        private val sidoAndSigunguFixture = fixture(listOf("서울 구로구", "경기 평택시", "경기 의정부시", "경기 시흥시", "경기 파주시"))

        val defaultGuest = fixture<Member> {
            property(Member::id) { 1L }
            property(Member::role) { MemberRole.MEMBER }
        }

        val defaultHost = fixture<Member> {
            property(Member::id) { 2L }
            property(Member::role) { MemberRole.HOST }
        }

        fun getSpaceFixtures(numberOfFixtures: Int, host: Member, status: SpaceStatus): List<Space> {
            return (1..numberOfFixtures).map { i ->
                fixture<Space> {
                    property(Space::id) { i.toLong() }
                    property(Address::sidoAndSigungu) { sidoAndSigunguFixture }
                    property(Space::defaultPeople) { minFixture }
                    property(Space::maxPeople) { maxFixture }
                    property(Space::status) { status }
                    property(Space::host) { host }
                }
            }
        }

        fun getImageFixtures(numberOfFixtures: Int, imageType: ImageType): List<Image> {
            return (1..numberOfFixtures).map { i ->
                fixture<Image> {
                    property(Image::id) { i.toLong() }
                    property(Image::imageUrl) { textFixture }
                    property(Image::type) { imageType }
                }
            }
        }

        fun getReviewFixtures(numberOfFixtures: Int, space: Space): List<Review> {
            return (1..numberOfFixtures).map { i ->
                fixture<Review> {
                    property(Review::id) { i.toLong() }
                    property(Review::content) { textFixture }
                    property(Review::rating) { 5 }
                    property(Review::member) { defaultGuest }
                    property(Review::space) { space }
                }
            }
        }
    }
}

