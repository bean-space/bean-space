package com.beanspace.beanspace.api.image

import com.beanspace.beanspace.api.image.dto.PhotoUpdateRequest
import com.beanspace.beanspace.api.image.dto.PostPhotoRequest
import com.beanspace.beanspace.api.image.dto.PostPhotoResponse
import com.beanspace.beanspace.api.image.dto.PreSignedUrlRequest
import com.beanspace.beanspace.api.image.dto.PreSignedUrlResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageService: ImageService
) {
    @PostMapping("/presigned-url")
    fun getPreSignedUrl(
        @RequestBody request: PreSignedUrlRequest
    ): ResponseEntity<PreSignedUrlResponse> {
        return ResponseEntity
            .ok(imageService.generatePreSignedUrl(request))
    }

    @PostMapping
    fun createPost(
        @RequestPart request: PostPhotoRequest,
        @RequestPart images: List<MultipartFile>
    ): ResponseEntity<PostPhotoResponse> {
        return ResponseEntity.ok()
            .body(imageService.createPhotoPost(request, images))
    }

    @PutMapping("/{photoPostId}")
    fun updatePostImages(
        @PathVariable photoPostId: Long,
        @RequestPart request: List<PhotoUpdateRequest>,
        @RequestPart images: List<MultipartFile>
    ): ResponseEntity<PostPhotoResponse> {
        val updatedPost = imageService.updatePostImages(photoPostId, request, images)
        return ResponseEntity.ok(updatedPost)
    }
}