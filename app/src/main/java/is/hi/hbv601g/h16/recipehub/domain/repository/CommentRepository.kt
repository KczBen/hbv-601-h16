package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentResponseDTO
import java.time.LocalDateTime
import java.util.UUID

class CommentRepository {

    companion object {
        private const val TAG = "CommentRepository"
    }

    suspend fun createComment(token: String, recipeUuid: UUID, textContent: String, images: Set<String>): Comment? {
        val request = CommentRequestDTO(textContent, images)
        return try {
            val response = NetworkModule.apiService.createComment("Bearer $token", recipeUuid, request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating comment", e)
            null
        }
    }

    suspend fun deleteComment(token: String, recipeUuid: UUID, commentUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteComment("Bearer $token", recipeUuid, commentUuid)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting comment", e)
            false
        }
    }

    suspend fun updateComment(token: String, recipeUuid: UUID, commentUuid: UUID, textContent: String, images: Set<String>): Comment? {
        val request = CommentRequestDTO(textContent, images)
        return try {
            val response = NetworkModule.apiService.updateComment("Bearer $token", recipeUuid, commentUuid, request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error updating comment", e)
            null
        }
    }

    suspend fun getComments(recipeId: UUID, page: Int, pageSize: Int): List<Comment> {
        return try {
            val response = NetworkModule.apiService.getComments(recipeId, page, pageSize)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting comments", e)
            emptyList()
        }
    }

    suspend fun getComment(recipeUuid: UUID, commentUuid: UUID): Comment? {
        return try {
            val response = NetworkModule.apiService.getComment(recipeUuid, commentUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting comment", e)
            null
        }
    }

    private fun mapToModel(dto: CommentResponseDTO): Comment {
        val owner = User(id = dto.ownerUuid)
        val recipe = Recipe(
            id = dto.recipeUuid,
            owner = User(id = UUID.randomUUID()), // Placeholder
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
