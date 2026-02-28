package `is`.hi.hbv601g.h16.recipehub.domain.controller

import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import java.util.Locale
import java.util.UUID

// dummy data class for simplicity
data class SearchResult(
    val id: UUID,
    val title: String,
    val owner: String,
    val categories: String,
)

class SearchController {

    //dummy data
    private val recipes = listOf(
        SearchResult(UUID.randomUUID(), "Chocolate Cake", "Bobby Tables", "Dessert" ),
        SearchResult(UUID.randomUUID(), "Pancakes", "Alice", "Dessert"),
        SearchResult(UUID.randomUUID(), "Chicken Curry", "Sam",  "Dinner")
    )

    fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return recipes
        return recipes.filter { recipe -> recipe.categories.contains(query, ignoreCase = true) ||
                recipe.title.contains(query, ignoreCase = true) }
    }

}