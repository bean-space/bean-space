package com.beanspace.beanspace.infra.s3.imagevalidator

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Component
class ImageValidator {
    // 허용되는 이미지 확장자 목록
    private val allowedExtensions = listOf(
        "jpg", "jpeg", "png", "gif", "webp", "jfif"
    )

    // 허용되는 MIME 타입 목록
    private val allowedMimeTypes = listOf(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    )

    // 허용되는 최대 이미지 용량 (application.yml 에도 최대 파일 크기가 따로 설정되어있음)
    private val maxFileSizeBytes = 10 * 1024 * 1024 // 10 메가 바이트

    fun isValidImage(file: MultipartFile): ValidationResult {
        val extensionValid = isValidExtension(file.originalFilename)
        val mimeTypeValid = isValidMimeType(file.contentType)
        val fileSizeValid = isValidFileSize(file.size)

        if (!extensionValid || !mimeTypeValid || !fileSizeValid) {
            return ValidationResult(
                isValid = false,
                reason = buildString {
                    if (!extensionValid) append("지원하지 않는 이미지 확장자입니다")
                    if (!mimeTypeValid) append("지원하지 않는 MIME 타입입니다")
                    if (!fileSizeValid) append("10MB 이상의 이미지를 업로드할 수 없습니다")
                }
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
            // 이미지의 바이트 데이터를 입력 스트림으로 변환
            val inputStream = ByteArrayInputStream(bytes)

            // 실제 이미지 파일인지 검증
            ImageIO.read(inputStream) ?: return ValidationResult(false, "이미지를 읽을 수 없습니다")

            ValidationResult(true, "이미지가 유효합니다")
        } catch (e: Exception) {
            // 이미지 처리 중 오류 발생 시
            ValidationResult(false, "이미지를 처리하는 중 오류가 발생하였습니다: ${e.message}")
        }
    }
}