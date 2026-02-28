package `is`.hi.hbv601g.h16.recipehub.model

import java.time.LocalDateTime
import java.util.UUID

data class Comment(
    val id: UUID,
    val owner: User,
    val recipe: Recipe,
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime,
    val textContent: String,
    val images: Set<String>,
) {
    init {
        require(textContent.isNotBlank()) {"Comment cannot be blank"}
    }
}