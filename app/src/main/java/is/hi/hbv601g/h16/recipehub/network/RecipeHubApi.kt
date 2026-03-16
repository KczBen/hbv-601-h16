package `is`.hi.hbv601g.h16.recipehub.network

import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecipeHubApi {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>

    @POST("signup")
    suspend fun signup(@Body request: SignupRequestDTO): Response<SignupResponseDTO>
}
