package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.host.dto.AddSpaceRequest
import com.beanspace.beanspace.api.host.dto.UpdateSpaceRequest
import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.exception.ModelNotFoundException
import com.beanspace.beanspace.domain.exception.NoPermissionException
import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.member.repository.MemberRepository
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HostService(
    private val spaceRepository: SpaceRepository,
    private val memberRepository: MemberRepository,
    private val imageRepository: ImageRepository,
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

        return SpaceResponse.from(savedSpace, imageUrlList)
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
            ?.let {
                SpaceResponse.from(it, request.imageUrlList)
            } ?: throw ModelNotFoundException(model = "Space", id = spaceId)
    }

    @Transactional
    fun deleteSpace(spaceId: Long, hostId: Long) {
        spaceRepository.findByIdOrNull(spaceId)
            ?.also { check(it.hasPermission(hostId)) { throw NoPermissionException() } }
            ?.also { imageRepository.deleteByTypeAndContentId(ImageType.SPACE, spaceId) }
            ?.also { it.delete() }
            ?: throw ModelNotFoundException(model = "Space", id = spaceId)
    }

    fun getSpaceList(hostId: Long): List<SpaceResponse> {
        return memberRepository.findByIdOrNull(hostId)
            ?.let { spaceRepository.findAllByHost(it) }
            ?.let {
                it.map { space ->
                    SpaceResponse.from(
                        space,
                        imageRepository.findAllByContentIdAndTypeOrderByOrderIndexAsc(space.id!!, ImageType.SPACE)
                            .map { image -> image.imageUrl })
                }
            }
            ?: listOf()
    }
}
