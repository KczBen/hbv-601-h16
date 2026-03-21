package `is`.hi.hbv601g.h16.recipehub.domain.repository

import android.util.Log
import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import java.util.UUID

data class AuthResult(
    val token: String,
    val userUuid: UUID
)

class AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun login(username: String, password: String): AuthResult? {
        return try {
            val response = NetworkModule.apiService.login(LoginRequestDTO(username, password))
            if (response.isSuccessful) {
                response.body()?.let { AuthResult(it.token, it.userUuid) }
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
                response.body()?.let { AuthResult(it.message, it.userUuid) }
            } else {
                Log.e(TAG, "Signup failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Signup exception", e)
            null
        }
    }
}
