package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookResponseDTO
import retrofit2.Response

import java.util.UUID

class RecipeBookRepository {
    suspend fun getRecipeBooks(page: Int, pageSize: Int): Response<List<RecipeBookResponseDTO>> {
        return NetworkModule.apiService.getRecipeBooks(page, pageSize)
    }

    suspend fun createRecipeBook(token: String, request: RecipeBookRequestDTO
    ): Response<RecipeBookResponseDTO> {
        return NetworkModule.apiService.createRecipeBook("Bearer $token", request)
    }

    suspend fun deleteRecipeBook(token: String, recipeBookUuid: UUID): Response<Void> {
        return NetworkModule.apiService.deleteRecipeBook("Bearer $token", recipeBookUuid)
    }

    suspend fun getRecipeBooksForUser(userUuid: UUID): Response<List<RecipeBookResponseDTO>> {
        return NetworkModule.apiService.getRecipeBooksForUser(userUuid)
    }

    suspend fun addRecipeToBook(token: String, recipeBookUuid: UUID, recipeUuid: UUID): Response<RecipeBookResponseDTO> {
        return NetworkModule.apiService.addRecipeToBook("Bearer $token", recipeBookUuid, recipeUuid)
    }

    suspend fun removeRecipeFromBook(token: String, recipeBookUuid: UUID, recipeUuid: UUID): Response<RecipeBookResponseDTO> {
        return NetworkModule.apiService.removeRecipeFromBook("Bearer $token", recipeBookUuid, recipeUuid)
    }
}