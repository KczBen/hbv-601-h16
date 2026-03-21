package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.CommentRepository
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CommentService {
    private val commentRepository = CommentRepository()

    suspend fun createComment(recipeId: UUID, textContent: String, images: Set<String> = emptySet()): Comment? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        commentRepository.createComment(token, recipeId, textContent, images)
    }

    suspend fun deleteComment(recipeId: UUID, commentId: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        commentRepository.deleteComment(token, recipeId, commentId)
    }

    suspend fun updateComment(recipeId: UUID, commentId: UUID, textContent: String, images: Set<String> = emptySet()): Comment? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        commentRepository.updateComment(token, recipeId, commentId, textContent, images)
    }

    suspend fun getComments(recipeId: UUID, page: Int, pageSize: Int): List<Comment> = withContext(Dispatchers.IO) {
        commentRepository.getComments(recipeId, page, pageSize)
    }

    suspend fun getComment(recipeId: UUID, commentId: UUID): Comment? = withContext(Dispatchers.IO) {
        commentRepository.getComment(recipeId, commentId)
    }
}
