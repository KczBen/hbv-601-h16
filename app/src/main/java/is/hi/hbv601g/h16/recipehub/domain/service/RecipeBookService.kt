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
        val request = RecipeBookRequestDTO(name, isPublic)
        recipeBookRepository.createRecipeBook(request)
    }

    suspend fun getByUser(ownerId: UUID): List<RecipeBook>? = withContext(Dispatchers.IO) {
        recipeBookRepository.getRecipeBooksForUser(ownerId)
    }

    suspend fun addRecipeToBook(bookId: UUID, recipeId: UUID): RecipeBook? = withContext(Dispatchers.IO) {
        recipeBookRepository.addRecipeToBook(bookId, recipeId)
    }

    suspend fun removeRecipeFromBook(bookId: UUID, recipeId: UUID): Boolean = withContext(Dispatchers.IO) {
        recipeBookRepository.removeRecipeFromBook(bookId, recipeId) != null
    }

    suspend fun deleteRecipeBook(bookId: UUID): Boolean = withContext(Dispatchers.IO) {
        recipeBookRepository.deleteRecipeBook(bookId)
    }
}
