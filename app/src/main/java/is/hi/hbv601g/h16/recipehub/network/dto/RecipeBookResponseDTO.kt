package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class RecipeBookResponseDTO(
    val recipeBookId: UUID,
    val ownerId: UUID,
    val name: String,
    val recipes: Set<RecipeResponseDTO>,
    val isPublic: Boolean
)
