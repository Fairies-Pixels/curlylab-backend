package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID? = UUID.randomUUID(),
    val username: String,
    val createdAt: LocalDateTime,
    val imageUrl: String? = null
)