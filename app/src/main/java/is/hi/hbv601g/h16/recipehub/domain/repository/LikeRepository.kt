package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LikeResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import retrofit2.Response
import java.util.UUID

class LikeRepository {

    suspend fun likeRecipe(token: String, recipeUuid: UUID): Response<RecipeResponseDTO> {
        return NetworkModule.apiService.likeRecipe("Bearer $token", recipeUuid)
    }

    suspend fun unlikeRecipe(token: String, recipeUuid: UUID): Response<RecipeResponseDTO> {
        return NetworkModule.apiService.unlikeRecipe("Bearer $token", recipeUuid)
    }

    suspend fun getLikesForRecipe(recipeUuid: UUID): Response<List<LikeResponseDTO>> {
        return NetworkModule.apiService.getLikesForRecipe(recipeUuid)
    }
}
