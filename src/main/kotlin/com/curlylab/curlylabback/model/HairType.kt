package com.curlylab.curlylabback.model

import java.util.UUID

data class HairType(
    val userId: UUID                                                                                             ,
    val porosity: Porosity?,
    val isColored: Boolean?,
    val thickness: Thickness?
)

enum class Porosity {
    HIGH,
    LOW,
    MEDIUM
}

enum class Thickness {
    THICK,
    MEDIUM,
    THIN
}