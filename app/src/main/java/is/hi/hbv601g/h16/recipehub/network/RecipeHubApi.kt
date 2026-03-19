package `is`.hi.hbv601g.h16.recipehub.network

import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.SignupResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.UserResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface RecipeHubApi {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>

    @POST("signup")
    suspend fun signup(@Body request: SignupRequestDTO): Response<SignupResponseDTO>

    // Recipe Endpoints
    @POST("recipes")
    suspend fun createRecipe(
        @Header("Authorization") token: String,
        @Body request: RecipeRequestDTO
    ): Response<Void>

    @DELETE("recipes/{recipe-uuid}")
    suspend fun deleteRecipe(
        @Header("Authorization") token: String,
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<Void>

    @PUT("recipes/{recipe-uuid}")
    suspend fun updateRecipe(
        @Header("Authorization") token: String,
        @Path("recipe-uuid") recipeUuid: UUID,
        @Body request: RecipeRequestDTO
    ): Response<RecipeResponseDTO>

    @GET("recipes")
    suspend fun getRecipes(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<RecipeResponseDTO>>

    @GET("recipes/{recipe-uuid}")
    suspend fun getRecipe(
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<RecipeResponseDTO>

    // User Endpoints
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<UserResponseDTO>>

    @GET("users/{user-uuid}")
    suspend fun getUser(
        @Path("user-uuid") userUuid: UUID
    ): Response<UserResponseDTO>

    @PUT("users/{user-uuid}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("user-uuid") userUuid: UUID,
        @Body request: UserRequestDTO
    ): Response<UserResponseDTO>

    @DELETE("users/{user-uuid}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("user-uuid") userUuid: UUID
    ): Response<Void>

    @POST("users/{user-uuid}/follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Path("user-uuid") userUuid: UUID
    ): Response<UserResponseDTO>

    @POST("users/{user-uuid}/unfollow")
    suspend fun unfollowUser(
        @Header("Authorization") token: String,
        @Path("user-uuid") userUuid: UUID
    ): Response<UserResponseDTO>

    // Comment Endpoints
    @POST("recipes/{recipe-uuid}/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("recipe-uuid") recipeUuid: UUID,
        @Body request: CommentRequestDTO
    ): Response<CommentResponseDTO>

    @DELETE("recipes/{recipe-uuid}/comments/{comment-uuid}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("recipe-uuid") recipeUuid: UUID,
        @Path("comment-uuid") commentUuid: UUID
    ): Response<Void>

    @PUT("recipes/{recipe-uuid}/comments/{comment-uuid}")
    suspend fun updateComment(
        @Header("Authorization") token: String,
        @Path("recipe-uuid") recipeUuid: UUID,
        @Path("comment-uuid") commentUuid: UUID,
        @Body request: CommentRequestDTO
    ): Response<CommentResponseDTO>

    @GET("recipes/{recipeId}/comments")
    suspend fun getComments(
        @Path("recipeId") recipeId: UUID,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<CommentResponseDTO>>

    @GET("recipes/{recipe-uuid}/comments/{comment-uuid}")
    suspend fun getComment(
        @Path("recipe-uuid") recipeUuid: UUID,
        @Path("comment-uuid") commentUuid: UUID
    ): Response<CommentResponseDTO>

    // Category Endpoints
    @POST("categories/{category-uuid}")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Path("category-uuid") categoryUuid: UUID,
        @Body request: CategoryRequestDTO
    ): Response<CategoryResponseDTO>

    @DELETE("categories/{category-uuid}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("category-uuid") categoryUuid: UUID
    ): Response<Void>

    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<CategoryResponseDTO>>

    @GET("categories/{category-name}")
    suspend fun getCategoryByName(
        @Path("category-name") categoryName: String
    ): Response<CategoryResponseDTO>

    @GET("categories/{category-name}/recipes")
    suspend fun getRecipesByCategoryName(
        @Path("category-name") categoryName: String
    ): Response<List<RecipeResponseDTO>>
}
