package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.*

data class UserProvider(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val provider: String,
    val providerUserId: String,
    val email: String?,
    val createdAt: LocalDateTime
)
