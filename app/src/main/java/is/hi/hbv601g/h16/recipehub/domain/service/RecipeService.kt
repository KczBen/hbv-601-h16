package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID

class RecipeService {
    fun createRecipe(recipe: Recipe) {
        MockRepository.recipes.add(recipe)
    }

    fun getSingleRecipe(id: UUID): Recipe? {
        return MockRepository.recipes.find { it.id == id }
    }

    fun findByTitle(title: String): Recipe? {
        return MockRepository.recipes.find { it.title == title }
    }

    fun deleteRecipe(user: User, recipe: Recipe) {
        val r = MockRepository.recipes.find { it.id == recipe.id }
        if (r != null && r.owner == user) {
            MockRepository.recipes.remove(r)
        }
    }

    fun getAllRecipes(page: Int, pageSize: Int): List<Recipe> {
        return MockRepository.recipes.toList()
            .sortedByDescending { it.creationDate }
            .drop(page * pageSize)
            .take(pageSize)
    }

    fun getRecipeByCategory(categories: Set<Category>): List<Recipe> {
        return MockRepository.recipes.filter { recipe ->
            recipe.categories.any { it in categories }
        }
    }

    fun getUserRecipes(owner: User): List<Recipe> {
        return MockRepository.recipes.filter { it.owner == owner }
    }

    fun modifyRecipe(user: User, recipe: Recipe) {
        val existing = MockRepository.recipes.find { it.id == recipe.id }
        if (existing != null && existing.owner == user) {
            MockRepository.recipes.remove(existing)
            MockRepository.recipes.add(recipe)
        }
    }
}
