package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.RecipeRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RecipeService {
    private val recipeRepository = RecipeRepository()
    private val categoryService = CategoryService()

    suspend fun createRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        recipeRepository.createRecipe(recipe)
    }

    suspend fun getSingleRecipe(id: UUID): Recipe? = withContext(Dispatchers.IO) {
        recipeRepository.getRecipe(id)
    }

    suspend fun deleteRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        recipeRepository.deleteRecipe(recipe.id)
    }

    suspend fun getAllRecipes(page: Int, pageSize: Int): List<Recipe> = withContext(Dispatchers.IO) {
        recipeRepository.getRecipes(page, pageSize)
    }

    suspend fun modifyRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        recipeRepository.updateRecipe(recipe.id, recipe) != null
    }

    suspend fun getRecipeByCategory(categories: Set<Category>): List<Recipe> {
        val categoryName = categories.firstOrNull()?.name ?: return emptyList()
        return categoryService.getRecipesForCategory(categoryName).toList()
    }
}
