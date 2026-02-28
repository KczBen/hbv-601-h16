package `is`.hi.hbv601g.h16.recipehub.model

import java.util.UUID

data class Category(
    val id: UUID,
    val name: String,
    val recipes: Set<Recipe> = setOf()
    ){

    init {
        require(name.isNotBlank()) {"The category must have a name"}
    }
}
