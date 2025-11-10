package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.UUID

data class Reviews (
    val reviewId: UUID? = UUID.randomUUID(),
    val userId: UUID,
    val productId: UUID,
    val date: LocalDateTime? = LocalDateTime.now(),
    val mark: ReviewMarks,
    val review: String? = "",
)

data class MarkAndReview (
    val mark: ReviewMarks? = null,
    val review: String? = null,
)

enum class ReviewMarks {
    ZERO_STARS,
    ONE_STAR,
    TWO_STARS,
    THREE_STARS,
    FOUR_STARS,
    FIVE_STARS,
}