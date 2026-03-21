package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recipe_books")
data class RecipeBookEntity(
    @PrimaryKey val id: UUID,
    val ownerId: UUID,
    val name: String,
    val isPublic: Boolean
)
