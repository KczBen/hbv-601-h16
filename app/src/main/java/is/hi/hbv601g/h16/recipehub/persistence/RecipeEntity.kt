package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: UUID,
    val ownerId: UUID,
    val title: String,
    val textContent: String,
    val imageInfos: Set<PersistedRecipeImage>,
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime,
    val rating: Float,
    val ratingCount: Long
)

data class PersistedRecipeImage(
    val path: String,
    val imageType: String
)
