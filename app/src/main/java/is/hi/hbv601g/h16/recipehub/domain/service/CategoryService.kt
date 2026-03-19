package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.CategoryRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class CategoryService {
    private val categoryRepository = CategoryRepository()
    private val userService = UserService()

    companion object {
        private const val TAG = "CategoryService"
    }

    suspend fun createCategory(categoryUuid: UUID, name: String): Category? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = CategoryRequestDTO(name)
        try {
            val response = categoryRepository.createCategory(token, categoryUuid, request)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating category", e)
        }
        return@withContext null
    }

    suspend fun deleteCategory(categoryUuid: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        try {
            val response = categoryRepository.deleteCategory(token, categoryUuid)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting category", e)
        }
        return@withContext false
    }

    suspend fun getAllCategories(page: Int, pageSize: Int): Set<Category> = withContext(Dispatchers.IO) {
        try {
            val response = categoryRepository.getCategories(page, pageSize)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it) }?.toSet() ?: emptySet()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting categories", e)
        }
        return@withContext emptySet()
    }

    // Kind of useless because we can just get all categories and do the find by name locally
    // Included for the sake of completeness
    suspend fun findCategoryByName(name: String): Category? = withContext(Dispatchers.IO) {
        try {
            val response = categoryRepository.getCategoryByName(name)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding category by name", e)
        }
        return@withContext null
    }

    suspend fun getRecipesForCategory(name: String): Set<Recipe> = withContext(Dispatchers.IO) {
        try {
            val response = categoryRepository.getRecipesByCategoryName(name)
            if (response.isSuccessful) {
                // Mapping each RecipeResponseDTO to Recipe model
                return@withContext response.body()?.map { mapRecipeToModel(it) }?.toSet() ?: emptySet()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes for category", e)
        }
        return@withContext emptySet()
    }

    private fun mapToModel(dto: CategoryResponseDTO): Category {
        return Category(id = dto.id, name = dto.name)
    }

    private suspend fun mapRecipeToModel(dto: RecipeResponseDTO): Recipe {
        val owner = userService.getUser(dto.ownerId) ?: `is`.hi.hbv601g.h16.recipehub.model.User(id = dto.ownerId)
        return Recipe(
            id = dto.recipeId,
            owner = owner,
            title = dto.title,
            textContent = dto.textContent,
            images = dto.imageUrls,
            // same issue here, need to fix it on the server side
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            rating = dto.rating,
            ratingCount = 0,
            categories = dto.categories.map { mapToModel(it) }.toSet()
        )
    }
}
