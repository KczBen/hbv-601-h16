package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserResponseDTO
import java.util.UUID

class UserRepository {

    companion object {
        private const val TAG = "UserRepository"
    }

    suspend fun getUsers(page: Int, pageSize: Int): List<User> {
        return try {
            val response = NetworkModule.apiService.getUsers(page, pageSize)
            if (response.isSuccessful) {
                response.body()?.map { mapToModel(it) } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting users", e)
            emptyList()
        }
    }

    suspend fun getUser(userUuid: UUID): User? {
        return try {
            val response = NetworkModule.apiService.getUser(userUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user", e)
            null
        }
    }

    suspend fun updateUser(userUuid: UUID, bio: String?, profilePictureUrl: String?): User? {
        val request = UserRequestDTO(profilePictureUrl, bio)
        return try {
            val response = NetworkModule.apiService.updateUser(userUuid, request)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
            null
        }
    }

    suspend fun deleteUser(userUuid: UUID): Boolean {
        return try {
            val response = NetworkModule.apiService.deleteUser(userUuid)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
            false
        }
    }

    suspend fun followUser(userUuid: UUID): User? {
        return try {
            val response = NetworkModule.apiService.followUser(userUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error following user", e)
            null
        }
    }

    suspend fun unfollowUser(userUuid: UUID): User? {
        return try {
            val response = NetworkModule.apiService.unfollowUser(userUuid)
            if (response.isSuccessful) {
                response.body()?.let { mapToModel(it) }
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error unfollowing user", e)
            null
        }
    }

    fun mapToModel(dto: UserResponseDTO): User {
        return User(
            id = dto.id,
            userName = dto.userName,
            profilePictureURL = dto.profilePictureUrl ?: "",
            bio = dto.bio ?: "",
            isBanned = dto.isBanned,
            isAdmin = dto.isAdmin,
            followers = dto.followers.map { User(id = it) }.toSet(),
            following = dto.following.map { User(id = it) }.toSet()
        )
    }
}
