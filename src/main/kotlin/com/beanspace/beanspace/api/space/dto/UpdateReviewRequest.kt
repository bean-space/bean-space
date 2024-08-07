package com.beanspace.beanspace.api.space.dto

import jakarta.validation.constraints.*

data class UpdateReviewRequest(
    @field:NotBlank(message = "리뷰는 1 ~ 3000까지 입력 가능합니다.")
    @field:Size(min = 1, max = 3000, message = "리뷰는 1 ~ 3000까지 입력 가능합니다")
    val content: String,

    @field:Min(value = 1, message = "별점은 1개 이상으로 입력해주세요.")
    @field:Max(value = 5, message = "별점은 5개 이하까지 가능합니다.")
    val rating: Int,

    @field:NotEmpty(message = "리뷰 이미지는 최대 3개까지 가능합니다.")
    @field:Size(max = 3, message = "리뷰 이미지는 최대 3개까지 가능합니다.")
    val imageUrlList: List<String>,
)