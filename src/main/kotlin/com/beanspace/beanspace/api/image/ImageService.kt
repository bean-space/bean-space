package com.beanspace.beanspace.api.image

import com.beanspace.beanspace.domain.image.model.Image
import com.beanspace.beanspace.domain.image.model.ImageType
import com.beanspace.beanspace.domain.image.model.PhotoPost
import com.beanspace.beanspace.domain.image.repository.ImageRepository
import com.beanspace.beanspace.domain.image.repository.PhotoPostRepository
import com.beanspace.beanspace.infra.s3.S3Service
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(
    val imageRepository: ImageRepository,
    val photoPostRepository: PhotoPostRepository,
    val s3Service: S3Service
) {
    fun generatePreSignedUrl(request: PreSignedUrlRequest): PreSignedUrlResponse {
        val preSignedUrl = s3Service.generatePreSignedUrl(request.fileName, request.contentType, ImageType.SPACE)
        return PreSignedUrlResponse(preSignedUrl)
    }

    @Transactional
    fun createPhotoPost(request: PostPhotoRequest, images: List<MultipartFile>): PostPhotoResponse {
        // try - catch 이용해서 여러 이미지 파일 업로드 중 하나가 실패하더라도 S3에 이미 올라간 이미지가 삭제되도록 작성

        val uploadedUrls = mutableListOf<String>()

        try {
            images.forEach { multipartFile ->
                val imageUrl = s3Service.uploadFile(multipartFile, ImageType.SPACE)
                uploadedUrls.add(imageUrl)
            }

            val photoPost = photoPostRepository.save(PhotoPost(title = request.title, content = request.content))

            uploadedUrls.forEachIndexed { index, imageUrl ->
                imageRepository.save(
                    Image(
                        type = ImageType.SPACE,
                        imageUrl = imageUrl,
                        contentId = photoPost.id!!,
                        orderIndex = index
                    )
                )
            }

            return PostPhotoResponse(
                title = photoPost.title,
                content = photoPost.content,
                imageUrlList = imageRepository.findAllByContentIdAndTypeOrderByOrderIndexAsc(
                    photoPost.id!!,
                    ImageType.SPACE
                ).map { it.imageUrl }
            )
        } catch (e: Exception) {

            uploadedUrls.forEach { url ->
                try {
                    s3Service.deleteFile("spaces/" + url.substringAfterLast('/'))
                } catch (deleteException: Exception) {
                    throw Exception("파일 삭제에 실패하였습니다: $url. 에러 메시지: ${deleteException.message}")
                }
            }

            throw e
        }
    }

    @Transactional
    fun updatePostImages(
        photoPostId: Long,
        request: List<PhotoUpdateRequest>,
        files: List<MultipartFile>
    ): PostPhotoResponse {
        // 중간에 이미지 업로드 실패시 복구시 적용이 필요함

        val photoPost = photoPostRepository.findByIdOrNull(photoPostId) ?: throw RuntimeException("못 찾음")

        val existingImages = imageRepository.findByContentIdOrderByOrderIndex(photoPostId)

        val fileMap = files.associateBy { it.originalFilename }

        request.forEach { photoUpdateRequest ->
            when {
                // 기존에 없던 새로운 이미지
                photoUpdateRequest.photoId == null && photoUpdateRequest.fileName != null -> {
                    val file = fileMap[photoUpdateRequest.fileName]
                        ?: throw IllegalArgumentException("해당 파일 찾을 수 없음: ${photoUpdateRequest.fileName}")
                    val imageUrl = s3Service.uploadFile(file, ImageType.SPACE)
                    imageRepository.save(
                        Image(
                            type = ImageType.SPACE,
                            imageUrl = imageUrl,
                            contentId = photoPostId,
                            orderIndex = photoUpdateRequest.orderIndex
                        )
                    )
                }
                // 기존에 있던 이미지 순서 바꾸기
                photoUpdateRequest.photoId != null -> {
                    val existingImage = existingImages.find { it.id == photoUpdateRequest.photoId }
                        ?: throw RuntimeException("못 찾음")
                    existingImage.orderIndex = photoUpdateRequest.orderIndex

                    // fileName이 DTO에 담겨져 보내졌으면 파일 교체
                    if (photoUpdateRequest.fileName != null) {
                        val file = fileMap[photoUpdateRequest.fileName]
                            ?: throw IllegalArgumentException("해당 파일 찾을 수 없음: ${photoUpdateRequest.fileName}")
                        val newImageUrl = s3Service.uploadFile(file, ImageType.SPACE)

                        // 이전 이미지 S3에서 제거
                        s3Service.deleteFile("spaces/" + existingImage.imageUrl.substringAfterLast('/'))

                        existingImage.imageUrl = newImageUrl
                    }

                    imageRepository.save(existingImage)
                }
                // 아마도 클라이언트에서 보낸 값이 잘못 됨
                else -> throw IllegalArgumentException("유효하지 않은 이미지 수정 요청입니다")
            }
        }

        // Request에 없는 이미지 삭제
        val requestPhotoIds = request.mapNotNull { it.photoId }.toSet()
        existingImages.filter { it.id !in requestPhotoIds }.forEach { image ->
            s3Service.deleteFile(image.imageUrl)
            imageRepository.delete(image)
        }

        return PostPhotoResponse(
            title = photoPost.title,
            content = photoPost.content,
            imageUrlList = imageRepository.findAllByContentIdAndTypeOrderByOrderIndexAsc(
                photoPost.id!!,
                ImageType.SPACE
            ).map { it.imageUrl }
        )
    }
}