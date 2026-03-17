package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserResponseDTO
import retrofit2.Response
import java.util.UUID

class UserRepository {

    suspend fun getUsers(page: Int, pageSize: Int): Response<List<UserResponseDTO>> {
        return NetworkModule.apiService.getUsers(page, pageSize)
    }

    suspend fun getUser(userUuid: UUID): Response<UserResponseDTO> {
        return NetworkModule.apiService.getUser(userUuid)
    }

    suspend fun updateUser(token: String, userUuid: UUID, request: UserRequestDTO): Response<UserResponseDTO> {
        return NetworkModule.apiService.updateUser("Bearer $token", userUuid, request)
    }

    suspend fun deleteUser(token: String, userUuid: UUID): Response<Void> {
        return NetworkModule.apiService.deleteUser("Bearer $token", userUuid)
    }

    suspend fun followUser(token: String, userUuid: UUID): Response<UserResponseDTO> {
        return NetworkModule.apiService.followUser("Bearer $token", userUuid)
    }

    suspend fun unfollowUser(token: String, userUuid: UUID): Response<UserResponseDTO> {
        return NetworkModule.apiService.unfollowUser("Bearer $token", userUuid)
    }
}
