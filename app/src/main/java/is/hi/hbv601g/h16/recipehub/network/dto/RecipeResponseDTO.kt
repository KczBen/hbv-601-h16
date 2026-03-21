package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class RecipeResponseDTO(
    val recipeId: UUID,
    val ownerId: UUID,
    val title: String,
    val textContent: String,
    val imageUrls: Set<String>,
    val creationDate: String?, // Assuming ISO-8601 string from server
    val editDate: String?,     // Assuming ISO-8601 string from server
    val likes: Int,
    val rating: Float,
    val ratingCount: Long?,
    val categories: Set<CategoryResponseDTO>
)
