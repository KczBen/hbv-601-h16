package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import java.time.LocalDateTime
import java.util.UUID

class CategoryRepository {

    companion object {
        private const val TAG = "CategoryRepository"
    }

    suspend fun createCategory(token: String, categoryUuid: UUID, name: String): Category? {
        val request = CategoryRequestDTO(name)
        return try {
            val response = NetworkModule.apiService.createCategory("Bearer $token", categoryUuid, request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating category", e)
            null
        }
    }

    suspend fun deleteCategory(token: String, categoryUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteCategory("Bearer $token", categoryUuid)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting category", e)
            false
        }
    }

    suspend fun getCategories(page: Int, pageSize: Int): List<Category> {
        return try {
            val response = NetworkModule.apiService.getCategories(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting categories", e)
            emptyList()
        }
    }

    suspend fun getCategoryByName(categoryName: String): Category? {
        return try {
            val response = NetworkModule.apiService.getCategoryByName(categoryName)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting category by name", e)
            null
        }
    }

    suspend fun getRecipesByCategoryName(categoryName: String): List<Recipe> {
        return try {
            val response = NetworkModule.apiService.getRecipesByCategoryName(categoryName)
            if (response.isSuccessful) {
                response.body()?.map { mapRecipeToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes by category name", e)
            emptyList()
        }
    }

    private fun mapToModel(dto: CategoryResponseDTO): Category {
        return Category(id = dto.id, name = dto.name)
    }

    private fun mapRecipeToModel(dto: RecipeResponseDTO): Recipe {
        return Recipe(
            id = dto.recipeId,
            owner = User(id = dto.ownerId),
            title = dto.title,
            textContent = dto.textContent,
            images = dto.imageUrls,
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            rating = dto.rating,
            ratingCount = 0,
            categories = dto.categories.map { mapToModel(it) }.toSet()
        )
    }
}
