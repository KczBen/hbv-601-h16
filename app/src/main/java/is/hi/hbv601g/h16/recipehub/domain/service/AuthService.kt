package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID

class AuthService {

    companion object {
        var currentUser: User? = null
    }

    /**
     * Verifies the given username and password.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return `true` if the credentials are valid, `false` otherwise.
     */
    fun login(username: String, password: String): Boolean {
        val user = MockRepository.users.find { it.userName == username }
        if (user != null && user.passwordHash == password) {
            currentUser = user
            return true
        }
        return false
    }

    fun registerUser(username: String, email: String, password: String) {
        val newUser = User(
            id = UUID.randomUUID(),
            userName = username,
            email = email,
            passwordHash = password,
            bio = ""
        )
        MockRepository.users.add(newUser)
        currentUser = newUser
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun logout() {
        currentUser = null
    }
}
