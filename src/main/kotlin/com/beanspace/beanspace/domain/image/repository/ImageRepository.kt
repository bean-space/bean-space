package com.beanspace.beanspace.domain.image.repository

import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository : JpaRepository<Image, Long> {
    fun findAllByContentIdAndTypeOrderByOrderIndexAsc(contentId: Long, contentType: ImageType): List<Image>
    fun findByContentIdOrderByOrderIndex(contentId: Long): List<Image>
}