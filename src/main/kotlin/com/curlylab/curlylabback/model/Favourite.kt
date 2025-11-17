package com.curlylab.curlylabback.model

import java.util.UUID

data class Favourite (
    val userId: UUID,
    val productId: UUID
)