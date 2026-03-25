package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookResponseDTO
import `is`.hi.hbv601g.h16.recipehub.persistence.PersistenceModule
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeBookRecipeCrossRef
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeCategoryCrossRef
import `is`.hi.hbv601g.h16.recipehub.persistence.toEntity
import `is`.hi.hbv601g.h16.recipehub.persistence.toModel
import java.util.UUID

class RecipeBookRepository {

    companion object {
        private const val TAG = "RecipeBookRepository"
    }

    private val recipeRepository = RecipeRepository()
    private val recipeBookDao = PersistenceModule.recipeBookDao
    private val recipeDao = PersistenceModule.recipeDao

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

    suspend fun createRecipeBook(request: RecipeBookRequestDTO): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.createRecipeBook(request)
            if (response.isSuccessful) {
                val book = response.body()?.let { mapToModel(it) }
                val currentUserId = AuthService.currentUser?.id
                if (book != null && book.owner.id == currentUserId) {
                    saveRecipeBooksLocally(book.owner.id, listOf(book))
                }
                book
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating recipe book", e)
            null
        }
    }

    suspend fun deleteRecipeBook(recipeBookUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteRecipeBook(recipeBookUuid)
            if (response.isSuccessful) {
                true
            } else false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe book", e)
            false
        }
    }

    suspend fun getRecipeBooksForUser(userUuid: UUID): List<RecipeBook> {
        return try {
            val response = NetworkModule.apiService.getRecipeBooksForUser(userUuid)
            if (response.isSuccessful) {
                val remoteBooks = response.body() ?: emptyList()
                val models = remoteBooks.map { mapToModel(it) }
                
                if (userUuid == AuthService.currentUser?.id) {
                    saveRecipeBooksLocally(userUuid, models)
                }
                
                models
            } else {
                fetchRecipeBooksLocally(userUuid)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe books for user, falling back to local", e)
            fetchRecipeBooksLocally(userUuid)
        }
    }

    private suspend fun saveRecipeBooksLocally(userUuid: UUID, books: List<RecipeBook>) {
        if (books.size > 1) {
            recipeBookDao.deleteRecipeBooksForUser(userUuid)
        }
        
        recipeBookDao.insertRecipeBooks(books.map { it.toEntity() })
        
        books.forEach { book ->
            val allRecipes = book.recipes.map { it.toEntity() }
            val crossRefs = book.recipes.map { RecipeBookRecipeCrossRef(book.id, it.id) }
            val categoryEntities = book.recipes.flatMap { it.categories }.map { it.toEntity() }.distinctBy { it.id }
            val categoryCrossRefs = book.recipes.flatMap { recipe -> 
                recipe.categories.map { category -> RecipeCategoryCrossRef(recipe.id, category.id) }
            }
            
            if (allRecipes.isNotEmpty()) {
                recipeDao.insertRecipes(allRecipes)
            }
            if (crossRefs.isNotEmpty()) {
                recipeBookDao.insertRecipeBookRecipeCrossRefs(crossRefs)
            }
            if (categoryEntities.isNotEmpty()) {
                recipeDao.insertCategories(categoryEntities)
            }
            if (categoryCrossRefs.isNotEmpty()) {
                recipeDao.insertRecipeCategoryCrossRefs(categoryCrossRefs)
            }
        }
    }

    private suspend fun fetchRecipeBooksLocally(userUuid: UUID): List<RecipeBook> {
        val currentUser = AuthService.currentUser ?: return emptyList()
        val books = recipeBookDao.getRecipeBooksByOwner(userUuid)
        return books.map { bookEntity ->
            val recipeEntities = recipeBookDao.getRecipesForBook(bookEntity.id)
            val recipes = recipeEntities.map { recipeEntity ->
                val categories = recipeDao.getCategoriesForRecipe(recipeEntity.id).map { it.toModel() }.toSet()
                recipeEntity.toModel(currentUser, categories)
            }.toSet()
            bookEntity.toModel(currentUser, recipes)
        }
    }

    suspend fun addRecipeToBook(recipeBookUuid: UUID, recipeUuid: UUID): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.addRecipeToBook(recipeBookUuid, recipeUuid)
            if (response.isSuccessful) {
                val book = response.body()?.let { mapToModel(it) }
                val currentUserId = AuthService.currentUser?.id
                if (book != null && book.owner.id == currentUserId) {
                    saveRecipeBooksLocally(book.owner.id, listOf(book))
                }
                book
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe to book", e)
            null
        }
    }

    suspend fun removeRecipeFromBook(recipeBookUuid: UUID, recipeUuid: UUID): RecipeBook? {
        return try {
            val response = NetworkModule.apiService.removeRecipeFromBook(recipeBookUuid, recipeUuid)
            if (response.isSuccessful) {
                val book = response.body()?.let { mapToModel(it) }
                val currentUserId = AuthService.currentUser?.id
                if (book != null && book.owner.id == currentUserId) {
                    recipeBookDao.deleteCrossRefsForBook(book.id)
                    saveRecipeBooksLocally(book.owner.id, listOf(book))
                }
                book
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error removing recipe from book", e)
            null
        }
    }

    private fun mapToModel(dto: RecipeBookResponseDTO): RecipeBook {
        return RecipeBook(
            id = dto.recipeBookId,
            owner = User(id = dto.ownerId), // Placeholder User
            name = dto.name,
            recipes = dto.recipes.map { recipeRepository.mapToModel(it) }.toSet(),
            isPublic = dto.isPublic
        )
    }
}
