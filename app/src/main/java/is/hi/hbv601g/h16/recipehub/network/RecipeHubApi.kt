package `is`.hi.hbv601g.h16.recipehub.network

import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CommentResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LikeResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.LoginResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeBookResponseDTO
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
        @Body request: RecipeRequestDTO
    ): Response<Void>

    @DELETE("recipes/{recipe-uuid}")
    suspend fun deleteRecipe(
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<Void>

    @PUT("recipes/{recipe-uuid}")
    suspend fun updateRecipe(
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
        @Path("user-uuid") userUuid: UUID,
        @Body request: UserRequestDTO
    ): Response<UserResponseDTO>

    @DELETE("users/{user-uuid}")
    suspend fun deleteUser(
        @Path("user-uuid") userUuid: UUID
    ): Response<Void>

    @POST("users/{user-uuid}/follow")
    suspend fun followUser(
        @Path("user-uuid") userUuid: UUID
    ): Response<UserResponseDTO>

    @POST("users/{user-uuid}/unfollow")
    suspend fun unfollowUser(
        @Path("user-uuid") userUuid: UUID
    ): Response<UserResponseDTO>

    // Comment Endpoints
    @POST("recipes/{recipe-uuid}/comments")
    suspend fun createComment(
        @Path("recipe-uuid") recipeUuid: UUID,
        @Body request: CommentRequestDTO
    ): Response<CommentResponseDTO>

    @DELETE("recipes/{recipe-uuid}/comments/{comment-uuid}")
    suspend fun deleteComment(
        @Path("recipe-uuid") recipeUuid: UUID,
        @Path("comment-uuid") commentUuid: UUID
    ): Response<Void>

    @PUT("recipes/{recipe-uuid}/comments/{comment-uuid}")
    suspend fun updateComment(
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
        @Path("category-uuid") categoryUuid: UUID,
        @Body request: CategoryRequestDTO
    ): Response<CategoryResponseDTO>

    @DELETE("categories/{category-uuid}")
    suspend fun deleteCategory(
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

    // RecipeBook Endpoints
    @POST("recipebooks")
    suspend fun createRecipeBook(
        @Body request: RecipeBookRequestDTO
    ): Response<RecipeBookResponseDTO>

    @DELETE("recipebooks/{recipebook-uuid}")
    suspend fun deleteRecipeBook(
        @Path("recipebook-uuid") recipeBookUuid: UUID
    ): Response<Void>

    @GET("recipebooks")
    suspend fun getRecipeBooks(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<RecipeBookResponseDTO>>

    @GET("recipebooks/{user-uuid}")
    suspend fun getRecipeBooksForUser(
        @Path("user-uuid") userUuid: UUID
    ): Response<List<RecipeBookResponseDTO>>

    @POST("recipebooks/{recipebook-uuid}/{recipe-uuid}")
    suspend fun addRecipeToBook(
        @Path("recipebook-uuid") recipeBookUuid: UUID,
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<RecipeBookResponseDTO>

    // this probably should not have been put but rather delete, oh well
    @PUT("recipebooks/{recipebook-uuid}/{recipe-uuid}")
    suspend fun removeRecipeFromBook(
        @Path("recipebook-uuid") recipeBookUuid: UUID,
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<RecipeBookResponseDTO>

    // Like Endpoints
    @POST("likes/recipe/{recipe-uuid}")
    suspend fun likeRecipe(
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<RecipeResponseDTO>

    @DELETE("likes/recipe/{recipe-uuid}")
    suspend fun unlikeRecipe(
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<RecipeResponseDTO>

    @GET("likes/recipe/{recipe-uuid}")
    suspend fun getLikesForRecipe(
        @Path("recipe-uuid") recipeUuid: UUID
    ): Response<List<LikeResponseDTO>>
}
