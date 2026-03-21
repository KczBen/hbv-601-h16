package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.Entity
import java.util.UUID

@Entity(tableName = "recipe_book_recipe_cross_ref", primaryKeys = ["recipeBookId", "recipeId"])
data class RecipeBookRecipeCrossRef(
    val recipeBookId: UUID,
    val recipeId: UUID
)
