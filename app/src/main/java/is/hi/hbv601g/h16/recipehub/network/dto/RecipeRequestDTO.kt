package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class RecipeRequestDTO(
    val title: String,
    val textContent: String,
    val imageData: List<ByteArray> = emptyList(),
    val imageType: List<String> = emptyList(),
    val categoryUuids: Set<UUID>
)
