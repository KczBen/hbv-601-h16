package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.time.LocalDateTime
import java.util.UUID

data class CommentResponseDTO(
    val id: UUID,
    val ownerUuid: UUID,
    val recipeUuid: UUID,
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime?,
    val textContent: String,
    val images: Set<String>?
)
