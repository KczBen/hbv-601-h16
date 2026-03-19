package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.domain.repository.RecipeBookRepository
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RecipeBookService {

    private val recipeBookRepository = RecipeBookRepository()

    private val userService = UserService()
    private val recipeService = RecipeService()

    companion object {
        private const val TAG = "RecipeBookService"
    }

    suspend fun createRecipeBook(name: String, isPublic: Boolean): RecipeBook? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = RecipeBookRequestDTO(name, isPublic)
        try {
            val response = recipeBookRepository.createRecipeBook(token, request)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating recipe book", e)
        }

        return@withContext null
    }

    suspend fun getByUser(ownerId: UUID): List<RecipeBook>? = withContext(Dispatchers.IO) {
        try {
            val response = recipeBookRepository.getRecipeBooksForUser(ownerId)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe books for user", e)
        }

        return@withContext null
    }

    suspend fun addRecipeToBook(bookId: UUID, recipeId: UUID): RecipeBook? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = recipeBookRepository.addRecipeToBook(token, bookId, recipeId)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe to recipe book", e)
        }

        return@withContext null
    }

    suspend fun removeRecipeFromBook(user: User, bookId: UUID, recipeId: UUID): Boolean? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = recipeBookRepository.removeRecipeFromBook(token, bookId, recipeId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error removing recipe from recipe book", e)
        }

        return@withContext false
    }

    suspend fun deleteRecipeBook(bookId: UUID): Boolean? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = recipeBookRepository.deleteRecipeBook(token, bookId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe book", e)
        }

        return@withContext false
    }

    private suspend fun mapToModel(dto: RecipeBookResponseDTO): RecipeBook {
        val owner = userService.getUser(dto.ownerId) ?: User(id = dto.ownerId)
        return RecipeBook(
            id = dto.recipeBookId,
            owner = owner,
            name = dto.name,
            recipes = dto.recipes.map { recipeService.mapToModel(it) }.toSet(),
            isPublic = dto.isPublic
        )
    }
}
