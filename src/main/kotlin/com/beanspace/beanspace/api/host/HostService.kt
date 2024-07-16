package com.beanspace.beanspace.api.host

import com.beanspace.beanspace.api.space.dto.SpaceResponse
import com.beanspace.beanspace.domain.space.repository.SpaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HostService(private val spaceRepository: SpaceRepository) {

    @Transactional
    fun createSpace(): SpaceResponse {
        TODO("Not yet implemented")
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
