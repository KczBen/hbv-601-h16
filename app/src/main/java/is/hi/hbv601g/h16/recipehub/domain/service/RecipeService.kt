package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.RecipeRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class RecipeService {
    private val recipeRepository = RecipeRepository()
    private val userService = UserService()
    private val categoryService = CategoryService()

    companion object {
        private const val TAG = "RecipeService"
    }

    suspend fun createRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        val request = RecipeRequestDTO(
            title = recipe.title,
            textContent = recipe.textContent,
            imageUrls = recipe.images,
            categoryUuids = recipe.categories.mapNotNull { it.id }.toSet()
        )
        try {
            val response = recipeRepository.createRecipe(token, request)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error creating recipe", e)
            return@withContext false
        }
    }

    suspend fun getSingleRecipe(id: UUID): Recipe? = withContext(Dispatchers.IO) {
        try {
            val response = recipeRepository.getRecipe(id)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe", e)
        }
        return@withContext null
    }

    suspend fun deleteRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        val id = recipe.id ?: return@withContext false
        try {
            val response = recipeRepository.deleteRecipe(token, id)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe", e)
            return@withContext false
        }
    }

    suspend fun getAllRecipes(page: Int, pageSize: Int): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val response = recipeRepository.getRecipes(page, pageSize)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it) } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes", e)
        }
        return@withContext emptyList()
    }

    suspend fun modifyRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        val id = recipe.id ?: return@withContext false
        val request = RecipeRequestDTO(
            title = recipe.title,
            textContent = recipe.textContent,
            imageUrls = recipe.images,
            categoryUuids = recipe.categories.mapNotNull { it.id }.toSet()
        )
        try {
            val response = recipeRepository.updateRecipe(token, id, request)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error updating recipe", e)
            return@withContext false
        }
    }

    // Temporary mapping logic
    suspend fun mapToModel(dto: RecipeResponseDTO): Recipe {
        val owner = userService.getUser(dto.ownerId) ?: User(id = dto.ownerId)
        return Recipe(
            id = dto.recipeId,
            owner = owner,
            title = dto.title,
            textContent = dto.textContent,
            images = dto.imageUrls,
            creationDate = LocalDateTime.now(), // Date not in DTO
            editDate = LocalDateTime.now(),     // Date not in DTO
            rating = dto.rating,
            ratingCount = 0, // Not in DTO
            categories = dto.categories.map { mapCategory(it) }.toSet()
        )
    }

    private fun mapCategory(dto: CategoryResponseDTO): Category {
        return Category(id = dto.id, name = dto.name)
    }

    // These methods might need further implementation or network support
    fun findByTitle(title: String): Recipe? = null

    suspend fun getRecipeByCategory(categories: Set<Category>): List<Recipe> {
        // If multiple categories are provided, we currently just fetch for the first one
        // as the backend endpoint is per category name.
        val categoryName = categories.firstOrNull()?.name ?: return emptyList()
        return categoryService.getRecipesForCategory(categoryName).toList()
    }

    fun getUserRecipes(owner: User): List<Recipe> = emptyList()
}
