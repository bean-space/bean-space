package com.beanspace.beanspace.api.image

import com.beanspace.beanspace.api.image.dto.PreSignedUrlRequest
import com.beanspace.beanspace.api.image.dto.PreSignedUrlResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}