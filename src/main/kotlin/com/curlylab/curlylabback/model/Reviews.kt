package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.UUID

data class Reviews (
    val reviewId: UUID? = UUID.randomUUID(),
    val userId: UUID?,
    var productId: UUID?,
    val date: LocalDateTime? = LocalDateTime.now(),
    val mark: Int?,
    val review: String? = "",
)

data class MarkAndReview (
    val mark: Int? = null,
    val review: String? = null,
)