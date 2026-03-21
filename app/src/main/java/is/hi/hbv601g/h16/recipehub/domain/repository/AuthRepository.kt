package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import `is`.hi.hbv601g.h16.recipehub.persistence.PersistenceModule
import `is`.hi.hbv601g.h16.recipehub.persistence.toEntity
import `is`.hi.hbv601g.h16.recipehub.persistence.toModel
import java.util.UUID

data class AuthResult(
    val token: String,
    val userUuid: UUID
)

class AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val userDao = PersistenceModule.userDao
    private val userRepository = UserRepository()

    suspend fun login(username: String, password: String): AuthResult? {
        return try {
            val response = NetworkModule.apiService.login(LoginRequestDTO(username, password))
            if (response.isSuccessful) {
                val result = response.body()?.let { AuthResult(it.token, it.userUuid) }
                if (result != null) {
                    // Sync user with local storage
                    val userResponse = NetworkModule.apiService.getUser(result.userUuid)
                    if (userResponse.isSuccessful) {
                        userResponse.body()?.let { dto ->
                            val user = userRepository.mapToModel(dto)
                            saveUserLocally(user.copy(isLoggedIn = true))
                        }
                    }
                }
                result
            } else {
                Log.e(TAG, "Login failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            null
        }
    }

    suspend fun signup(username: String, email: String, password: String): AuthResult? {
        return try {
            val response = NetworkModule.apiService.signup(SignupRequestDTO(username, email, password))
            if (response.isSuccessful) {
                val result = response.body()?.let { AuthResult(it.message, it.userUuid) }
                if (result != null) {
                    val userResponse = NetworkModule.apiService.getUser(result.userUuid)
                    if (userResponse.isSuccessful) {
                        userResponse.body()?.let { dto ->
                            val user = userRepository.mapToModel(dto)
                            saveUserLocally(user.copy(isLoggedIn = true))
                        }
                    }
                }
                result
            } else {
                Log.e(TAG, "Signup failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Signup exception", e)
            null
        }
    }

    suspend fun getLoggedInUser(): User? {
        return userDao.getLoggedInUser()?.toModel()
    }

    suspend fun logout() {
        userDao.logoutAll()
    }

    private suspend fun saveUserLocally(user: User) {
        userDao.logoutAll()
        userDao.insertUser(user.toEntity())
    }
}
