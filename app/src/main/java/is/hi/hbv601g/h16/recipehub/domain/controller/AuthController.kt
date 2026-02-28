package `is`.hi.hbv601g.h16.recipehub.domain.controller

import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID

class AuthController {

    // dummy data
    private val users = arrayOf(User(UUID.randomUUID(), "Bobby Tables", "bobby@example.com", "password123"))

    /**
     * Verifies the given username and password.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return `true` if the credentials are valid, `false` otherwise.
     */
    fun login(username: String, password: String): Boolean {
        val expectedPassword = users.find { it.userName == username }?.passwordHash

        return expectedPassword == password
    }


}