package com.curlylab.curlylabback.model

import java.time.LocalDateTime
import java.util.*

data class UserAuth(
    val userId: UUID,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val createdAt: LocalDateTime
)