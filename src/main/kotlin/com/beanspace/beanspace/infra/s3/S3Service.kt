package com.beanspace.beanspace.infra.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.beanspace.beanspace.domain.exception.InvalidImageException
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.infra.s3.imagevalidator.ImageValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.Date
import java.util.UUID

@Component
class S3Service(
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val amazonS3: AmazonS3,
    private val imageValidator: ImageValidator
) {
    fun uploadFile(file: MultipartFile, imageType: ImageType): String {
        // 이미지 파일 유효성 검증 결과
        val validationResult = imageValidator.isValidImage(file)

        if (!validationResult.isValid) {
            throw InvalidImageException("${file.originalFilename ?: "알 수 없는 파일"}: ${validationResult.reason}")
        }

        // S3 저장소에 저장할 이미지 경로 (imageType에 따라 정해짐)
        val directory = when (imageType) {
            ImageType.REVIEW -> "reviews"
            ImageType.SPACE -> "spaces"
        }

        // UUID를 이용해서 파일 이름이 겹치지 않도록 함
        val filename = "${UUID.randomUUID()}-${file.originalFilename}"

        val key = "$directory/$filename"

        val metadata = ObjectMetadata().apply {
            contentType = determineContentType(file)
            contentLength = file.size
            contentDisposition = "inline"
        }

        amazonS3.putObject(bucket, key, file.inputStream, metadata)

        return amazonS3.getUrl(bucket, key).toString()
    }

    fun deleteFile(key: String) {
        amazonS3.deleteObject(bucket, key)
    }

    fun generatePreSignedUrl(fileName: String, contentType: String, imageType: ImageType): String {
        val expiration = Date(System.currentTimeMillis() + 900000) // 15분 뒤에 URL 만료

        val directory = when (imageType) {
            ImageType.REVIEW -> "reviews"
            ImageType.SPACE -> "spaces"
        }

        val filename = "${UUID.randomUUID()}-${fileName}"

        val key = "$directory/$filename"

        val metaData = ObjectMetadata()
        metaData.contentType = contentType
        metaData.contentDisposition = "inline"

        val generatePreSignedUrlRequest = GeneratePresignedUrlRequest(bucket, key)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)
            .withContentType(contentType)

        return amazonS3.generatePresignedUrl(generatePreSignedUrlRequest).toString()
    }

    private fun determineContentType(file: MultipartFile): String {
        // 파일에 저장되어 있는 컨텐츠 타입
        val contentType = file.contentType

        // 콘텐츠 타입이 비어 있거나 기본값인 경우 파일 확장자를 기반으로 콘텐츠 타입을 결정
        return if (contentType.isNullOrBlank() || contentType == "application/octet-stream") {
            determineContentTypeByExtension(file.originalFilename)
        } else {
            contentType
        }
    }

    // 파일 확장자를 기반으로 콘텐츠 타입을 결정하는 함수
    private fun determineContentTypeByExtension(filename: String?): String {
        return when (filename?.substringAfterLast('.', "")?.lowercase()) {
            "jpg", "jpeg", "jfif" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
    }
}