package com.beanspace.beanspace.domain.space.service

import com.beanspace.beanspace.domain.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpaceService(private val spaceRepository: SpaceRepository) {
    fun getSpaceList(): List<SpaceResponse> {
        //TODO 이미지 가져와야함
        return spaceRepository.findAll().map { SpaceResponse.from(it) }
    }

    fun getSpace(spaceId: Long): SpaceResponse {
        val space = spaceRepository.findByIdOrNull(spaceId) ?: throw Exception() //TODO Custom Exception 적용
        //TODO 이미지 가져와야함
        return SpaceResponse.from(space)
    }

    @Transactional
    fun createSpace(): SpaceResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    fun updateSpace(): SpaceResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    fun deleteSpace() {
        TODO("Not yet implemented")
    }
}
