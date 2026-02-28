package `is`.hi.hbv601g.h16.recipehub.model

import java.util.UUID

data class Rating(
    val id: UUID,
    val owner: User,
    val recipe: Recipe,
    val value: Int
    ) {
}