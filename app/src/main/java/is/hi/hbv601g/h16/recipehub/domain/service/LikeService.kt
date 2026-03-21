package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.LikeRepository
import `is`.hi.hbv601g.h16.recipehub.model.Like
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class LikeService {
    private val likeRepository = LikeRepository()

    suspend fun getLikesForRecipe(recipeUuid: UUID): List<Like> = withContext(Dispatchers.IO) {
        likeRepository.getLikesForRecipe(recipeUuid)
    }

    suspend fun likeRecipe(recipeUuid: UUID): Recipe? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        likeRepository.likeRecipe(token, recipeUuid)
    }

    suspend fun unlikeRecipe(recipeUuid: UUID): Recipe? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        likeRepository.unlikeRecipe(token, recipeUuid)
    }
}
