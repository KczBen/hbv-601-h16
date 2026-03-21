package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.Entity
import java.util.UUID

@Entity(tableName = "recipe_category_cross_ref", primaryKeys = ["recipeId", "categoryId"])
data class RecipeCategoryCrossRef(
    val recipeId: UUID,
    val categoryId: UUID
)
