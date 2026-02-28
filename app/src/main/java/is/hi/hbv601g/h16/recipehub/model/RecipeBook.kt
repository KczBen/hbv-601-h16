package `is`.hi.hbv601g.h16.recipehub.model

import java.util.UUID

data class RecipeBook(
    val id: UUID,
    val owner: User,
    val name: String,
    val recipes: Set<Recipe> = setOf(),
    val isPublic: Boolean
) {
    init {
        require(name.isNotBlank()) {"Recipe Book must have a title"}
    }
}