package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.*

data class UserRefreshToken(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val revoked: Boolean = false
)