package com.curlylab.curlylabback.model

import java.util.UUID

data class Favourite (
    var userId: UUID?,
    val productId: UUID
)