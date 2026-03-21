package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.LikeRepository
import `is`.hi.hbv601g.h16.recipehub.model.Like
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.LikeResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class LikeService {
    private val likeRepository = LikeRepository()
    private val recipeService = RecipeService()
    private val userService = UserService()

    companion object {
        private const val TAG = "LikeService"
    }

    suspend fun getLikesForRecipe(recipeUuid: UUID): List<Like> = withContext(Dispatchers.IO) {
        try {
            val response = likeRepository.getLikesForRecipe(recipeUuid)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it, recipeUuid) } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting likes for recipe", e)
        }
        return@withContext emptyList()
    }

    suspend fun likeRecipe(recipeUuid: UUID): Recipe? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = likeRepository.likeRecipe(token, recipeUuid)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { recipeService.mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error liking recipe", e)
        }
        return@withContext null
    }

    suspend fun unlikeRecipe(recipeUuid: UUID): Recipe? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = likeRepository.unlikeRecipe(token, recipeUuid)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { recipeService.mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unliking recipe", e)
        }
        return@withContext null
    }

    private suspend fun mapToModel(dto: LikeResponseDTO, recipeUuid: UUID): Like {
        val owner = userService.getUser(dto.ownerUuid) ?: User(id = dto.ownerUuid)
        // this shouldn't really come up because a like will always be related to a recipe,
        // but it does technically need one
        val recipe = Recipe(
            id = recipeUuid,
            owner = User(id = UUID.randomUUID()),
            title = "Shell Recipe",
            textContent = "Shell Content",
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
