package com.beanspace.beanspace.infra.s3.imagevalidator

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Component
class ImageValidator {
    private val allowedExtensions = listOf(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "svg"
    )

    private val allowedMimeTypes = listOf(
        "image/jpeg", "image/png", "image/gif", "image/bmp",
        "image/webp", "image/tiff", "image/svg+xml"
    )

    private val maxFileSizeBytes = 10 * 1024 * 1024 // 10 MB

    fun isValidImage(file: MultipartFile): ValidationResult {
        val extensionValid = isValidExtension(file.originalFilename)
        val mimeTypeValid = isValidMimeType(file.contentType)
        val fileSizeValid = isValidFileSize(file.size)

        if (!extensionValid || !mimeTypeValid || !fileSizeValid) {
            return ValidationResult(
                isValid = false,
                reason = buildString {
                    if (!extensionValid) append("Invalid file extension. ")
                    if (!mimeTypeValid) append("Invalid MIME type. ")
                    if (!fileSizeValid) append("File size exceeds limit. ")
                }.trim()
            )
        }

        return isValidImageContent(file.bytes)
    }

    private fun isValidExtension(filename: String?): Boolean {
        if (filename == null) return false
        val extension = filename.substringAfterLast('.', "").lowercase()
        return extension in allowedExtensions
    }

    private fun isValidMimeType(mimeType: String?): Boolean {
        return mimeType in allowedMimeTypes
    }

    private fun isValidFileSize(sizeBytes: Long): Boolean {
        return sizeBytes <= maxFileSizeBytes
    }

    private fun isValidImageContent(bytes: ByteArray): ValidationResult {
        return try {
            val inputStream = ByteArrayInputStream(bytes)

            val bufferedImage =
                ImageIO.read(inputStream) ?: return ValidationResult(false, "Unable to read image content.")


            ValidationResult(true, "Image is valid.")
        } catch (e: Exception) {
            ValidationResult(false, "Error processing image: ${e.message}")
        }
    }
}