package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.Like
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LikeResponseDTO
import java.time.LocalDateTime
import java.util.UUID

class LikeRepository {

    companion object {
        private const val TAG = "LikeRepository"
    }

    private val recipeRepository = RecipeRepository()

    suspend fun likeRecipe(recipe: Recipe): Recipe? {
        return try {
            val response = NetworkModule.apiService.likeRecipe(recipe.id)
            if (response.isSuccessful) {
                val resp = response.body()?.let { recipeRepository.mapToModel(it) }
                recipe.likes = resp!!.likes
                return recipe
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error liking recipe", e)
            null
        }
    }

    suspend fun unlikeRecipe(recipe: Recipe): Recipe? {
        return try {
            val response = NetworkModule.apiService.unlikeRecipe(recipe.id)
            if (response.isSuccessful) {
                val resp = response.body()?.let { recipeRepository.mapToModel(it) }
                recipe.likes = resp!!.likes
                return recipe
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error unliking recipe", e)
            null
        }
    }

    suspend fun getLikesForRecipe(recipeUuid: UUID): List<Like> {
        return try {
            val response = NetworkModule.apiService.getLikesForRecipe(recipeUuid)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it, recipeUuid) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting likes for recipe", e)
            emptyList()
        }
    }

    private fun mapToModel(dto: LikeResponseDTO, recipeUuid: UUID): Like {
        val owner = User(id = dto.ownerUuid)
        val recipe = Recipe(
            id = recipeUuid,
            owner = User(id = UUID.randomUUID()), // Placeholder
            title = "Shell Recipe",
            textContent = "Shell Content",
            images = emptySet(),
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            rating = 0f,
            ratingCount = 0L
        )
        return Like(
            id = dto.id,
            owner = owner,
            recipe = recipe
        )
    }
}
