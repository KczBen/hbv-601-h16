package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupResponseDTO
import retrofit2.Response

class AuthRepository {

    suspend fun login(request: LoginRequestDTO): Response<LoginResponseDTO> {
        return NetworkModule.apiService.login(request)
    }

    suspend fun signup(request: SignupRequestDTO): Response<SignupResponseDTO> {
        return NetworkModule.apiService.signup(request)
    }
}
