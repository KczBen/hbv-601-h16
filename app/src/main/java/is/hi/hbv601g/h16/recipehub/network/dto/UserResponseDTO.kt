package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class UserResponseDTO(
    val id: UUID,
    val userName: String,
    val profilePictureUrl: String?,
    val bio: String?,
    val myRecipes: Set<UUID>,
    val recipeBooks: Set<UUID>,
    val likedRecipes: Set<UUID>,
    val following: Set<UUID>,
    val followers: Set<UUID>,
    val isBanned: Boolean,
    val isAdmin: Boolean
)
