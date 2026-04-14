package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.time.LocalDateTime
import java.util.UUID

data class RecipeResponseDTO(
    val recipeId: UUID,
    val ownerId: UUID,
    val title: String,
    val textContent: String,
    val images: Set<RecipeImageResponseDTO>,
    val creationDate: LocalDateTime?,
    val editDate: LocalDateTime?,
    val likes: Int,
    val rating: Float,
    val ratingCount: Long?,
    val categories: Set<CategoryResponseDTO>
) {
    data class RecipeImageResponseDTO(
        val data: ByteArray,
        val imageType: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RecipeImageResponseDTO

            if (!data.contentEquals(other.data)) return false
            if (imageType != other.imageType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.contentHashCode()
            result = 31 * result + imageType.hashCode()
            return result
        }
    }
}
