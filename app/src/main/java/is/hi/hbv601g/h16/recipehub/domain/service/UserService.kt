package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.UserRepository
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class UserService {
    private val userRepository = UserRepository()

    companion object {
        private const val TAG = "UserService"
    }

    suspend fun getUsers(page: Int, pageSize: Int): List<User> = withContext(Dispatchers.IO) {
        try {
            val response = userRepository.getUsers(page, pageSize)
            if (response.isSuccessful) {
                return@withContext response.body()?.map { mapToModel(it) } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting users", e)
        }
        return@withContext emptyList()
    }

    suspend fun getUser(id: UUID): User? = withContext(Dispatchers.IO) {
        try {
            val response = userRepository.getUser(id)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user", e)
        }
        return@withContext null
    }

    suspend fun updateUser(id: UUID, bio: String?, profilePictureUrl: String?): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        val request = UserRequestDTO(profilePictureUrl, bio)
        try {
            val response = userRepository.updateUser(token, id, request)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
        }
        return@withContext null
    }

    suspend fun deleteUser(id: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        try {
            val response = userRepository.deleteUser(token, id)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
        }
        return@withContext false
    }

    suspend fun followUser(id: UUID): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = userRepository.followUser(token, id)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error following user", e)
        }
        return@withContext null
    }

    suspend fun unfollowUser(id: UUID): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        try {
            val response = userRepository.unfollowUser(token, id)
            if (response.isSuccessful) {
                return@withContext response.body()?.let { mapToModel(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unfollowing user", e)
        }
        return@withContext null
    }

    private fun mapToModel(dto: UserResponseDTO): User {
        return User(
            id = dto.id,
            userName = dto.userName,
            profilePictureURL = dto.profilePictureUrl ?: "",
            bio = dto.bio ?: "",
            isBanned = dto.isBanned,
            isAdmin = dto.isAdmin
            // Note: In a full implementation, we'd map the Sets of UUIDs to the corresponding models if needed,
            // but for now, we follow the minimal modification principle.
        )
    }
}
