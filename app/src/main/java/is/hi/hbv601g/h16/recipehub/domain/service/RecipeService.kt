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
        val token = AuthService.token ?: return@withContext false
        recipeRepository.createRecipe(token, recipe)
    }

    suspend fun getSingleRecipe(id: UUID): Recipe? = withContext(Dispatchers.IO) {
        recipeRepository.getRecipe(id)
    }

    suspend fun deleteRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        recipeRepository.deleteRecipe(token, recipe.id)
    }

    suspend fun getAllRecipes(page: Int, pageSize: Int): List<Recipe> = withContext(Dispatchers.IO) {
        recipeRepository.getRecipes(page, pageSize)
    }

    suspend fun modifyRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        recipeRepository.updateRecipe(token, recipe.id, recipe) != null
    }

    // These methods might need further implementation or network support
    fun findByTitle(title: String): Recipe? = null

    suspend fun getRecipeByCategory(categories: Set<Category>): List<Recipe> {
        val categoryName = categories.firstOrNull()?.name ?: return emptyList()
        return categoryService.getRecipesForCategory(categoryName).toList()
    }

    fun getUserRecipes(owner: User): List<Recipe> = emptyList()
}
