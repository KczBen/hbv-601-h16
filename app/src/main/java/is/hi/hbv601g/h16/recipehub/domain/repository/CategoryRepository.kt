package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.network.NetworkModule
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryResponseDTO
import `is`.hi.hbv601g.h16.recipehub.network.dto.RecipeResponseDTO
import retrofit2.Response
import java.util.UUID

class CategoryRepository {

    suspend fun createCategory(token: String, categoryUuid: UUID, request: CategoryRequestDTO): Response<CategoryResponseDTO> {
        return NetworkModule.apiService.createCategory("Bearer $token", categoryUuid, request)
    }

    suspend fun deleteCategory(token: String, categoryUuid: UUID): Response<Void> {
        return NetworkModule.apiService.deleteCategory("Bearer $token", categoryUuid)
    }

    suspend fun getCategories(page: Int, pageSize: Int): Response<List<CategoryResponseDTO>> {
        return NetworkModule.apiService.getCategories(page, pageSize)
    }

    suspend fun getCategoryByName(categoryName: String): Response<CategoryResponseDTO> {
        return NetworkModule.apiService.getCategoryByName(categoryName)
    }

    suspend fun getRecipesByCategoryName(categoryName: String): Response<List<RecipeResponseDTO>> {
        return NetworkModule.apiService.getRecipesByCategoryName(categoryName)
    }
}
