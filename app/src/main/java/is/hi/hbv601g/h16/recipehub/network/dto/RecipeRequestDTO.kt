package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class RecipeRequestDTO(
    val title: String,
    val textContent: String,
    val imageUrls: Set<String>,
    val categoryUuids: Set<UUID>
)
