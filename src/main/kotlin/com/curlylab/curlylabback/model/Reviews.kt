package com.curlylab.curlylabback.model

import java.util.UUID

data class Reviews (
    val userId: UUID,
    val productId: UUID,
    val mark: ReviewMarks,
    val review: String,
)

enum class ReviewMarks {
    ZERO_STARS,
    ONE_STAR,
    TWO_STARS,
    THREE_STARS,
    FOUR_STARS,
    FIVE_STARS,
}