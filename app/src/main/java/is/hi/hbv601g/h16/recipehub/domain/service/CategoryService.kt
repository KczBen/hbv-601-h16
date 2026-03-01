package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import java.util.UUID

class CategoryService(
    private val categoryRepository: MockRepository = MockRepository
) {
    fun createCategory(category: Category) {
        val c = category.copy(id = UUID.randomUUID())
        categoryRepository.categories.add(c)
    }

    fun deleteCategory(category: Category) {

    }

    fun findCategoryByID(id: UUID): Category? {
        return categoryRepository.categories.find { it.id == id }
    }

    fun findCategoryByName(name: String): Category? {
        return categoryRepository.categories.find { it.name == name }
    }

    fun getAllCategories(page: Int, pageSize: Int): Set<Category> {
        return categoryRepository.categories.toSet()
    }

    fun updateCategory(category: Category) {

    }

    fun getRecipesForCategory(categoryId: UUID): Set<Recipe> {
        return categoryRepository.categories.find { it.id == categoryId }?.recipes?.toSet() ?: emptySet()
    }

    fun getRecipesForCategory(name: String): Set<Recipe> {
        return categoryRepository.categories.find { it.name == name }?.recipes?.toSet() ?: emptySet()
    }
}