package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import java.time.LocalDateTime
import java.util.UUID

class RecipeRepository {

    companion object {
        private const val TAG = "RecipeRepository"
    }

    suspend fun createRecipe(recipe: Recipe): Boolean {
        val request = RecipeRequestDTO(
            title = recipe.title,
            textContent = recipe.textContent,
            imageUrls = recipe.images,
            categoryUuids = recipe.categories.map { it.id }.toSet()
        )
        return try {
            val response = NetworkModule.apiService.createRecipe(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error creating recipe", e)
            false
        }
    }

    suspend fun deleteRecipe(recipeUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteRecipe(recipeUuid)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe", e)
            false
        }
    }

    suspend fun updateRecipe(recipeUuid: UUID, recipe: Recipe): Recipe? {
        val request = RecipeRequestDTO(
            title = recipe.title,
            textContent = recipe.textContent,
            imageUrls = recipe.images,
            categoryUuids = recipe.categories.map { it.id }.toSet()
        )
        return try {
            val response = NetworkModule.apiService.updateRecipe(recipeUuid, request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error updating recipe", e)
            null
        }
    }

    suspend fun getRecipes(page: Int, pageSize: Int): List<Recipe> {
        return try {
            val response = NetworkModule.apiService.getRecipes(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes", e)
            emptyList()
        }
    }

    suspend fun getRecipe(recipeUuid: UUID): Recipe? {
        return try {
            val response = NetworkModule.apiService.getRecipe(recipeUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe", e)
            null
        }
    }

    fun mapToModel(dto: RecipeResponseDTO): Recipe {
        return Recipe(
            id = dto.recipeId,
            owner = User(id = dto.ownerId),
            title = dto.title,
            textContent = dto.textContent,
            images = dto.imageUrls,
            creationDate = dto.creationDate ?: LocalDateTime.now(),
            editDate = dto.editDate ?: LocalDateTime.now(),
            rating = dto.rating,
            ratingCount = dto.ratingCount ?: 0L,
            categories = dto.categories.map { mapCategory(it) }.toSet()
        )
    }

    private fun mapCategory(dto: CategoryResponseDTO): Category {
        return Category(id = dto.id, name = dto.name)
    }
}
