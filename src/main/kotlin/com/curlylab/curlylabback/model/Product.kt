package com.curlylab.curlylabback.model

import java.util.UUID

data class Product(
    val id: UUID? = UUID.randomUUID(),
    val name: String,
    val description: String,
    val tags: List<String>,
    val imageUrl: String? = null
)

