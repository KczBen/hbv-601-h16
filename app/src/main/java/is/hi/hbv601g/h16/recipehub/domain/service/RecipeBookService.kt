package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.RecipeBookRepository
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RecipeBookService {

    private val recipeBookRepository = RecipeBookRepository()

    suspend fun createRecipeBook(name: String, isPublic: Boolean): RecipeBook? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = RecipeBookRequestDTO(name, isPublic)
        recipeBookRepository.createRecipeBook(token, request)
    }

    suspend fun getByUser(ownerId: UUID): List<RecipeBook>? = withContext(Dispatchers.IO) {
        recipeBookRepository.getRecipeBooksForUser(ownerId)
    }

    suspend fun addRecipeToBook(bookId: UUID, recipeId: UUID): RecipeBook? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        recipeBookRepository.addRecipeToBook(token, bookId, recipeId)
    }

    suspend fun removeRecipeFromBook(bookId: UUID, recipeId: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        recipeBookRepository.removeRecipeFromBook(token, bookId, recipeId) != null
    }

    suspend fun deleteRecipeBook(bookId: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        recipeBookRepository.deleteRecipeBook(token, bookId)
    }
}
