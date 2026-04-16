package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.content.Context
import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.RecipeHubApplication
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import `is`.hi.hbv601g.h16.recipehub.persistence.PersistenceModule
import `is`.hi.hbv601g.h16.recipehub.persistence.toEntity
import `is`.hi.hbv601g.h16.recipehub.persistence.toModel
import java.util.UUID
import androidx.core.content.edit

/**
 * Represents the result of an authentication operation (login or signup)
 * 
 * @property token The JWT token returned by the server upon successful authentication (or the success message in case of signup)
 * @property userUuid The unique identifier (UUID) of the authenticated user
 */
data class AuthResult(
    val token: String,
    val userUuid: UUID
)

/**
 * Repository responsible for managing authentication processes, including 
 * login, signup, and local session persistence
 */
class AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
    }

    private val userDao = PersistenceModule.userDao
    private val userRepository = UserRepository()
    private val sharedPrefs =
        RecipeHubApplication.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Authenticates a user with the provided credentials
     * 
     * On successful login:
     * 1. The JWT token is saved
     * 2. User details are fetched from the server
     * 3. The user is saved locally
     * 
     * @param username The username of the user attempting to login
     * @param password The password of the user attempting to login
     * @return An AuthResult containing the token and user UUID if successful; null otherwise
     */
    suspend fun login(username: String, password: String): AuthResult? {
        return try {
            val response = NetworkModule.apiService.login(LoginRequestDTO(username, password))
            if (response.isSuccessful) {
                val result = response.body()?.let { AuthResult(it.token, it.userUuid) }
                if (result != null) {
                    saveToken(result.token)
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

    /**
     * Registers a new user with the provided details
     * 
     * On successful signup:
     * 1. The server's success message (which contains the token) is saved
     * 2. User details are fetched from the server
     * 3. The user is saved locally
     * 
     * @param username The username for the new account
     * @param email The email address for the new account
     * @param password The password for the new account
     * @return An AuthResult containing the JWT and user UUID if successful; null otherwise
     */
    suspend fun signup(username: String, email: String, password: String): AuthResult? {
        return try {
            val response = NetworkModule.apiService.signup(SignupRequestDTO(username, email, password))
            if (response.isSuccessful) {
                val result = response.body()?.let { AuthResult(it.message, it.userUuid) }
                if (result != null) {
                    saveToken(result.token)
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

    /**
     * Retrieves the currently stored JWT token
     * 
     * @return The saved JWT token string, or null if no token is found
     */
    fun getSavedToken(): String? {
        return sharedPrefs.getString(KEY_TOKEN, null)
    }

    /**
     * Saves the provided JWT token
     * 
     * @param token The JWT token to be saved
     */
    private fun saveToken(token: String) {
        sharedPrefs.edit { putString(KEY_TOKEN, token) }
    }

    /**
     * Retrieves the currently logged-in user from local storage
     * 
     * @return The User object if a user is currently logged in; null otherwise
     */
    suspend fun getLoggedInUser(): User? {
        return userDao.getLoggedInUser()?.toModel()
    }

    /**
     * Logs out the current user
     * 1. Clears all local user session data
     * 2. Removes the stored JWT token
     */
    suspend fun logout() {
        userDao.logoutAll()
        sharedPrefs.edit { remove(KEY_TOKEN) }
    }

    /**
     * Saves the user data to local storage
     * 
     * @param user The User object to be saved locally
     */
    private suspend fun saveUserLocally(user: User) {
        userDao.logoutAll()
        userDao.insertUser(user.toEntity())
    }
}
