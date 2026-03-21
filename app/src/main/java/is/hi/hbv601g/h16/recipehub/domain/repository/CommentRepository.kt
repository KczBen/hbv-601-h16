package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentResponseDTO
import retrofit2.Response
import java.util.UUID

class CommentRepository {

    suspend fun createComment(token: String, recipeUuid: UUID, request: CommentRequestDTO): Response<CommentResponseDTO> {
        return NetworkModule.apiService.createComment("Bearer $token", recipeUuid, request)
    }

    suspend fun deleteComment(token: String, recipeUuid: UUID, commentUuid: UUID): Response<Void> {
        return NetworkModule.apiService.deleteComment("Bearer $token", recipeUuid, commentUuid)
    }

    suspend fun updateComment(token: String, recipeUuid: UUID, commentUuid: UUID, request: CommentRequestDTO): Response<CommentResponseDTO> {
        return NetworkModule.apiService.updateComment("Bearer $token", recipeUuid, commentUuid, request)
    }

    suspend fun getComments(recipeId: UUID, page: Int, pageSize: Int): Response<List<CommentResponseDTO>> {
        return NetworkModule.apiService.getComments(recipeId, page, pageSize)
    }

    suspend fun getComment(recipeUuid: UUID, commentUuid: UUID): Response<CommentResponseDTO> {
        return NetworkModule.apiService.getComment(recipeUuid, commentUuid)
    }
}
