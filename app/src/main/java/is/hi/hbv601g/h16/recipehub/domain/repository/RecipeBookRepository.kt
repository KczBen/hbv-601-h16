package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookResponseDTO
import java.util.UUID

class RecipeBookRepository {

    companion object {
        private const val TAG = "RecipeBookRepository"
    }

    private val recipeRepository = RecipeRepository()

    suspend fun getRecipeBooks(page: Int, pageSize: Int): List<RecipeBook> {
        return try {
            val response = NetworkModule.apiService.getRecipeBooks(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe books", e)
            emptyList()
        }
    }

    suspend fun createRecipeBook(token: String, request: RecipeBookRequestDTO): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.createRecipeBook("Bearer $token", request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating recipe book", e)
            null
        }
    }

    suspend fun deleteRecipeBook(token: String, recipeBookUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteRecipeBook("Bearer $token", recipeBookUuid)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe book", e)
            false
        }
    }

    suspend fun getRecipeBooksForUser(userUuid: UUID): List<RecipeBook> {
        return try {
            val response = NetworkModule.apiService.getRecipeBooksForUser(userUuid)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe books for user", e)
            emptyList()
        }
    }

    suspend fun addRecipeToBook(token: String, recipeBookUuid: UUID, recipeUuid: UUID): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.addRecipeToBook("Bearer $token", recipeBookUuid, recipeUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe to book", e)
            null
        }
    }

    suspend fun removeRecipeFromBook(token: String, recipeBookUuid: UUID, recipeUuid: UUID): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.removeRecipeFromBook("Bearer $token", recipeBookUuid, recipeUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error removing recipe from book", e)
            null
        }
    }

    private fun mapToModel(dto: RecipeBookResponseDTO): RecipeBook {
        return RecipeBook(
            id = dto.recipeBookId,
            owner = User(id = dto.ownerId),
            name = dto.name,
            recipes = dto.recipes.map { recipeRepository.mapToModel(it) }.toSet(),
            isPublic = dto.isPublic
        )
    }
}
