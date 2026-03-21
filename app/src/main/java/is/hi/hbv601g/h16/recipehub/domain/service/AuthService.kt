package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.AuthRepository
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService {

    private val authRepository = AuthRepository()
    private val userService = UserService()

    companion object {
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

        val result = authRepository.login(username, password)
        if (result != null) {
            token = result.token
            // Fetch full user details after login
            val userDetails = userService.getUser(result.userUuid)
            currentUser = userDetails ?: User(
                id = result.userUuid,
                userName = username,
                email = "",
                passwordHash = ""
            )
            return@withContext true
        }
        return@withContext false
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        if (username.isBlank() || email.isBlank() || validatePassword(password).isNotEmpty()) {
            return@withContext false
        }

        val result = authRepository.signup(username, email, password)
        if (result != null) {
            // Automatically login after successful signup to get the token
            return@withContext login(username, password)
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
