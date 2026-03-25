package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.time.LocalDateTime
import java.util.UUID

data class RecipeResponseDTO(
    val recipeId: UUID,
    val ownerId: UUID,
    val title: String,
    val textContent: String,
    val imageUrls: Set<String>,
    val creationDate: LocalDateTime?,
    val editDate: LocalDateTime?,
    val likes: Int,
    val rating: Float,
    val ratingCount: Long?,
    val categories: Set<CategoryResponseDTO>
)
