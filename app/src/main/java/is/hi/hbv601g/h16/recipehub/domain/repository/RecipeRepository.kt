package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import retrofit2.Response
import java.util.UUID

class RecipeRepository {

    suspend fun createRecipe(token: String, request: RecipeRequestDTO): Response<Void> {
        return NetworkModule.apiService.createRecipe("Bearer $token", request)
    }

    suspend fun deleteRecipe(token: String, recipeUuid: UUID): Response<Void> {
        return NetworkModule.apiService.deleteRecipe("Bearer $token", recipeUuid)
    }

    suspend fun updateRecipe(token: String, recipeUuid: UUID, request: RecipeRequestDTO): Response<RecipeResponseDTO> {
        return NetworkModule.apiService.updateRecipe("Bearer $token", recipeUuid, request)
    }

    suspend fun getRecipes(page: Int, pageSize: Int): Response<List<RecipeResponseDTO>> {
        return NetworkModule.apiService.getRecipes(page, pageSize)
    }

    suspend fun getRecipe(recipeUuid: UUID): Response<RecipeResponseDTO> {
        return NetworkModule.apiService.getRecipe(recipeUuid)
    }
}
