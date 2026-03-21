package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.CommentRepository
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class CommentService {
    private val commentRepository = CommentRepository()
    private val userService = UserService()
    private val recipeService = RecipeService()

    companion object {
        private const val TAG = "CommentService"
    }

    suspend fun createComment(recipeId: UUID, textContent: String, images: Set<String> = emptySet()): Comment? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = CommentRequestDTO(textContent, images)
        try {
            val response = commentRepository.createComment(token, recipeId, request)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating comment", e)
        }
        return@withContext null
    }

    suspend fun deleteComment(recipeId: UUID, commentId: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        try {
            val response = commentRepository.deleteComment(token, recipeId, commentId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting comment", e)
        }
        return@withContext false
    }

    suspend fun updateComment(recipeId: UUID, commentId: UUID, textContent: String, images: Set<String> = emptySet()): Comment? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = CommentRequestDTO(textContent, images)
        try {
            val response = commentRepository.updateComment(token, recipeId, commentId, request)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating comment", e)
        }
        return@withContext null
    }

    suspend fun getComments(recipeId: UUID, page: Int, pageSize: Int): List<Comment> = withContext(Dispatchers.IO) {
        try {
            val response = commentRepository.getComments(recipeId, page, pageSize)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it) } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting comments", e)
        }
        return@withContext emptyList()
    }

    suspend fun getComment(recipeId: UUID, commentId: UUID): Comment? = withContext(Dispatchers.IO) {
        try {
            val response = commentRepository.getComment(recipeId, commentId)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting comment", e)
        }
        return@withContext null
    }

    private suspend fun mapToModel(dto: CommentResponseDTO): Comment {
        val owner = userService.getUser(dto.ownerUuid) ?: User(id = dto.ownerUuid)
        
        // Technically a comment needs a recipe, but we don't actually *need* one
        // so make a fake one and just attach that
        // if it's ACTUALLY needed then request it separately later
        val recipe = Recipe(
            id = dto.recipeUuid,
            owner = owner, // Placeholder
            title = "",
            textContent = "",
            creationDate = dto.creationDate,
            editDate = dto.creationDate,
            rating = 0f,
            ratingCount = 0
        )

        return Comment(
            id = dto.id,
            owner = owner,
            recipe = recipe,
            creationDate = dto.creationDate,
            editDate = dto.editDate ?: dto.creationDate,
            textContent = dto.textContent,
            images = dto.images
        )
    }
}
