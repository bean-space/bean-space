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
    fun updateSpace(spaceId: Long): SpaceResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    fun deleteSpace(spaceId: Long) {
        TODO("Not yet implemented")
    }
}
