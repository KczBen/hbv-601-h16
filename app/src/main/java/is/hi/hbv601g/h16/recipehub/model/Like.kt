package `is`.hi.hbv601g.h16.recipehub.model

import java.util.UUID

data class Like(
    val id: UUID,
    val owner: User,
    val recipe: Recipe,
    ) {
}