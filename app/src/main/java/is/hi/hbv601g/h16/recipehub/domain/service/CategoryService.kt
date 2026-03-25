package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.CategoryRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.network.dto.CategoryRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CategoryService {
    private val categoryRepository = CategoryRepository()

    suspend fun createCategory(categoryUuid: UUID, name: String): Category? = withContext(Dispatchers.IO) {
        categoryRepository.createCategory(categoryUuid, name)
    }

    suspend fun deleteCategory(categoryUuid: UUID): Boolean = withContext(Dispatchers.IO) {
        categoryRepository.deleteCategory(categoryUuid)
    }

    suspend fun getAllCategories(page: Int, pageSize: Int): Set<Category> = withContext(Dispatchers.IO) {
        categoryRepository.getCategories(page, pageSize).toSet()
    }

    suspend fun findCategoryByName(name: String): Category? = withContext(Dispatchers.IO) {
        categoryRepository.getCategoryByName(name)
    }

    suspend fun getRecipesForCategory(name: String): Set<Recipe> = withContext(Dispatchers.IO) {
        categoryRepository.getRecipesByCategoryName(name).toSet()
    }
}
