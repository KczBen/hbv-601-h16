package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.UserRepository
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class UserService {
    private val userRepository = UserRepository()

    suspend fun getUsers(page: Int, pageSize: Int): List<User> = withContext(Dispatchers.IO) {
        userRepository.getUsers(page, pageSize)
    }

    suspend fun getUser(id: UUID): User? = withContext(Dispatchers.IO) {
        userRepository.getUser(id)
    }

    suspend fun updateUser(id: UUID, bio: String?, profilePictureUrl: String?): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        userRepository.updateUser(token, id, bio, profilePictureUrl)
    }

    suspend fun deleteUser(id: UUID): Boolean = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext false
        userRepository.deleteUser(token, id)
    }

    suspend fun followUser(id: UUID): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        userRepository.followUser(token, id)
    }

    suspend fun unfollowUser(id: UUID): User? = withContext(Dispatchers.IO) {
        val token = AuthService.token ?: return@withContext null
        userRepository.unfollowUser(token, id)
    }
}
