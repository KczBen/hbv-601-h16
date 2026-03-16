package `is`.hi.hbv601g.h16.recipehub.domain.service

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.domain.repository.AuthRepository
import `is`.hi.hbv601g.h16.recipehub.model.User
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService {

    private val authRepository = AuthRepository()

    companion object {
        private const val TAG = "AuthService"
        var currentUser: User? = null
        // Save the token for all future requests. Everything related to creating data needs one
        var token: String? = null
    }

    /**
     * Verifies the given username and password.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return `true` if the credentials are valid, `false` otherwise.
     */
    suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || password.isBlank()) return@withContext false

        try {
            val response = authRepository.login(LoginRequestDTO(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    token = body.token
                    currentUser = User(
                        id = body.userUuid,
                        userName = username,
                        email = "",
                        passwordHash = ""
                    )
                    Log.d(TAG, "Login successful for $username")
                    return@withContext true
                }
            } else {
                Log.e(TAG, "Login failed: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
        }
        return@withContext false
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || email.isBlank() || validatePassword(password).isNotEmpty()) {
            return@withContext false
        }

        try {
            val response = authRepository.signup(SignupRequestDTO(username, email, password))
            if (response.isSuccessful) {
                Log.d(TAG, "Signup successful for $username, attempting login...")
                // Automatically login after successful signup to get the token
                return@withContext login(username, password)
            } else {
                Log.e(TAG, "Signup failed: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Signup exception", e)
        }
        return@withContext false
    }

    // These are the same as in the server code. Validate before we send a request.
    fun validatePassword(password: String): List<String> {
        val issues = mutableListOf<String>()
        if (password.length < 8) {
            issues.add("at least 8 characters")
        }
        if (!password.matches(Regex(".*\\p{Lu}.*"))) {
            issues.add("one or more uppercase letters")
        }
        if (!password.matches(Regex(".*\\p{Ll}.*"))) {
            issues.add("one or more lowercase letters")
        }
        if (!password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"))) {
            issues.add("one or more special symbols")
        }
        if (!password.matches(Regex(".*\\d.*"))) {
            issues.add("one or more digits")
        }
        return issues
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    // Logging out is not implemented yet on the UI side
    fun logout() {
        currentUser = null
        token = null
    }
}
