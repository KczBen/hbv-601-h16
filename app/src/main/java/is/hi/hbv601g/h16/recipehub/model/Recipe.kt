package `is`.hi.hbv601g.h16.recipehub.model

import java.time.LocalDateTime
import java.util.UUID

data class Recipe(
    val id: UUID,
    val owner: User,
    val title: String,
    val textContent: String,
    val images: Set<String> = setOf(),
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime,
    val likes: Set<Like> = setOf(),
    val rating: Float,
    val ratingCount: Long,
    val comments: Set<Comment> = setOf(),
    val categories: Set<Category> = setOf()
) {
    companion object {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_TEXT_LENGTH= 5000
    }
    init {
        require(title.isNotBlank()) {"Title cannot be empty"}
        require(title.length <= MAX_TITLE_LENGTH) {"Title cannot exceed $MAX_TITLE_LENGTH characters"}
        require(textContent.isNotBlank()) {"Content cannot be empty"}
        require(textContent.length <= MAX_TEXT_LENGTH) {"Content cannot exceed $MAX_TEXT_LENGTH characters"}
    }
}
