package com.beanspace.beanspace.infra.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.beanspace.beanspace.domain.exception.InvalidImageException
import com.beanspace.beanspace.domain.photo.model.ImageType
import com.beanspace.beanspace.infra.s3.imagevalidator.ImageValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.UUID
import javax.imageio.ImageIO

@Component
class S3Service(
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val amazonS3: AmazonS3,
    private val imageValidator: ImageValidator
) {
    fun uploadFile(file: MultipartFile, imageType: ImageType): String {
        val validationResult = imageValidator.isValidImage(file)

        if (!validationResult.isValid) {
            throw InvalidImageException("${file.originalFilename ?: "알수 없는 파일"}: ${validationResult.reason}")
        }

        val directory = when (imageType) {
            ImageType.PROFILE -> "profiles"
            ImageType.REVIEW -> "reviews"
            ImageType.SPACE -> "spaces"
        }

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

    fun deleteFile(fileUrl: String) {
        amazonS3.deleteObject(bucket, fileUrl.substringAfterLast('/'))
    }

    private fun determineContentType(file: MultipartFile): String {
        val contentType = file.contentType

        return if (contentType.isNullOrBlank() || contentType == "application/octet-stream") {
            determineContentTypeByExtension(file.originalFilename)
        } else {
            contentType
        }
    }

    private fun determineContentTypeByExtension(filename: String?): String {
        return when (filename?.substringAfterLast('.', "")?.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            "bmp" -> "image/bmp"
            "tiff", "tif" -> "image/tiff"
            else -> "application/octet-stream"
        }
    }

    private fun isValidImage(bytes: ByteArray): Boolean {
        return try {
            val inputStream = ByteArrayInputStream(bytes)
            val bufferedImage = ImageIO.read(inputStream)
            bufferedImage != null
        } catch (e: Exception) {
            false
        }
    }
}