package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.host.dto.AddSpaceRequest
import com.beanspace.beanspace.api.host.dto.UpdateSpaceRequest
import com.beanspace.beanspace.api.reservation.dto.ReservationResponse
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.reservation.repository.ReservationRepository
import com.beanspace.beanspace.domain.space.model.SpaceOffer
import com.beanspace.beanspace.domain.space.repository.OfferRepository
import com.beanspace.beanspace.domain.space.repository.ReviewRepository
import com.beanspace.beanspace.domain.space.repository.SpaceOfferRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import com.beanspace.beanspace.domain.space.repository.WishListRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class HostService(
    private val spaceRepository: SpaceRepository,
    private val memberRepository: MemberRepository,
    private val imageRepository: ImageRepository,
    private val reservationRepository: ReservationRepository,
    private val reviewRepository: ReviewRepository,
    private val wishListRepository: WishListRepository,
    private val offerRepository: OfferRepository,
    private val spaceOfferRepository: SpaceOfferRepository

) {

    @Transactional
    fun createSpace(request: AddSpaceRequest, hostId: Long): SpaceResponse {
        val host = memberRepository.findByIdOrNull(hostId) ?: throw ModelNotFoundException(model = "Host", id = hostId)
        val savedSpace = spaceRepository.save(AddSpaceRequest.toEntity(request, host))

        val imageUrlList = request.imageUrlList

        request.imageUrlList.forEachIndexed { index, imageUrl ->
            imageRepository.save(
                Image(
                    type = ImageType.SPACE,
                    contentId = savedSpace.id!!,
                    imageUrl = imageUrl,
                    orderIndex = index
                )
            )
        }

        val spaceOffer = request.offer

        spaceOffer?.forEach {
            offerRepository.findByIdOrNull(it)
                ?.also { offer -> spaceOfferRepository.save(SpaceOffer(savedSpace, offer)) }
                ?: throw ModelNotFoundException("Offer", it)
        }

        return SpaceResponse.from(savedSpace, imageUrlList, spaceOffer)
    }

    @Transactional
    fun updateSpace(spaceId: Long, request: UpdateSpaceRequest, hostId: Long): SpaceResponse {
        return spaceRepository.findByIdOrNull(spaceId)
            ?.also { check(it.hasPermission(hostId)) { throw NoPermissionException() } }
            ?.also {
                it.update(
                    listingName = request.listingName,
                    price = request.price,
                    content = request.content,
                    defaultPeople = request.defaultPeople,
                    maxPeople = request.maxPeople,
                    pricePerPerson = request.pricePerPerson,
                    bedRoomCount = request.bedRoomCount,
                    bedCount = request.bedCount,
                    bathRoomCount = request.bathRoomCount,
                )
            }
            ?.also { imageRepository.deleteByTypeAndContentId(ImageType.SPACE, spaceId) }
            ?.also {
                request.imageUrlList.forEachIndexed { index, imageUrl ->
                    imageRepository.save(
                        Image(
                            type = ImageType.SPACE,
                            contentId = spaceId,
                            imageUrl = imageUrl,
                            orderIndex = index
                        )
                    )
                }
            }
            ?.also { spaceOfferRepository.deleteAllBySpaceId(it.id!!) }
            ?.also {
                request.offer?.forEach { id ->
                    offerRepository.findByIdOrNull(id)
                        ?.also { offer -> spaceOfferRepository.save(SpaceOffer(it, offer)) }
                        ?: throw ModelNotFoundException("Offer", id)
                }
            }
            ?.let {
                SpaceResponse.from(it, request.imageUrlList, request.offer)
            } ?: throw ModelNotFoundException(model = "Space", id = spaceId)
    }

    @Transactional
    fun deleteSpace(spaceId: Long, hostId: Long) {
        spaceRepository.findByIdOrNull(spaceId)
            ?.also { check(it.hasPermission(hostId)) { throw NoPermissionException() } }
            ?.also { imageRepository.deleteByTypeAndContentId(ImageType.SPACE, spaceId) }
            ?.also { spaceOfferRepository.deleteAllBySpaceId(spaceId) }
            ?.also { reservationRepository.findAllBySpaceId(spaceId).onEach { it.cancelReservation() } }
            ?.also { reviewRepository.findAllBySpaceId(spaceId).onEach { it.delete() } }
            ?.also { wishListRepository.deleteAllBySpaceId(spaceId) }
            ?.also { it.delete() }
            ?: throw ModelNotFoundException(model = "Space", id = spaceId)
    }

    fun getSpaceList(hostId: Long): List<SpaceResponse> {
        return memberRepository.findByIdOrNull(hostId)
            ?.let { spaceRepository.findAllByHostAndIsDeleted(it, false) }
            ?.let {
                it.map { space ->
                    SpaceResponse.from(
                        space,
                        imageRepository.findAllByContentIdAndTypeOrderByOrderIndexAsc(space.id!!, ImageType.SPACE)
                            .map { image -> image.imageUrl },
                        spaceOfferRepository.findAllBySpaceIdOrderById(space.id!!)
                            .map { spaceOffer -> spaceOffer.offer.id!! }
                    )
                }
            }
            ?: listOf()
    }

    fun getReservationList(hostId: Long): List<ReservationResponse> {
        val date = LocalDate.now().minusMonths(6)
        return spaceRepository.findAllByHostId(hostId)
            .map { it.id!! }
            .let { reservationRepository.findAllBySpaceIdInAndCheckOutAfter(it, date) }
            .map { ReservationResponse.from(it) }
            .sortedBy { it.checkIn }
    }
}
