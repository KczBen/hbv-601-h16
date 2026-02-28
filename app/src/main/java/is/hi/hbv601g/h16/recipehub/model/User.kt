package `is`.hi.hbv601g.h16.recipehub.model

import java.util.UUID

data class User(
    val id: UUID,
    val userName: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val profilePictureURL: String = "",
    val bio: String = "",
    val recipes: Set<Recipe> = setOf(),
    val recipeBooks: Set<RecipeBook> = setOf(),
    val likedRecipes: Set<Recipe> = setOf(),
    val followers: Set<User> = setOf(),
    val following: Set<User> = setOf(),
    val isBanned: Boolean = false,
    val isAdmin: Boolean = false
) {
    init {
        require(userName.isNotBlank()) {"Username cannot be empty"}
        require(email.isNotBlank()) {"Email address cannot be empty"}
        require(passwordHash.isNotBlank()) {"Password cannot be empty"}
    }
}