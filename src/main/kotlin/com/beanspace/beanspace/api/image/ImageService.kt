package com.beanspace.beanspace.api.image

import com.beanspace.beanspace.api.image.dto.PreSignedUrlRequest
import com.beanspace.beanspace.api.image.dto.PreSignedUrlResponse
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.infra.s3.S3Service
import org.springframework.stereotype.Service

@Service
class ImageService(
    val s3Service: S3Service
) {
    fun generatePreSignedUrl(request: PreSignedUrlRequest): PreSignedUrlResponse {
        val imageType = when (request.imageType) {
            "REVIEW" -> ImageType.REVIEW
            "SPACE" -> ImageType.SPACE
            "PROFILE" -> ImageType.PROFILE
            else -> throw IllegalArgumentException("이미지 타입을 올바르게 입력해주세요")
        }
        val preSignedUrl = s3Service.generatePreSignedUrl(request.fileName, request.contentType, imageType)
        return PreSignedUrlResponse(preSignedUrl)
    }
}