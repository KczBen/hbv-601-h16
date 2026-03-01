package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID

class RecipeBookService {
    fun createRecipeBook(recipeBook: RecipeBook) {
        MockRepository.recipeBooks.add(recipeBook)
    }

    fun getRecipeBook(id: UUID): RecipeBook? {
        return MockRepository.recipeBooks.find { it.id == id }
    }

    fun getUserRecipeBooks(owner: User): List<RecipeBook> {
        return MockRepository.recipeBooks.filter { it.owner == owner }
    }

    fun addRecipeToBook(bookId: UUID, recipe: Recipe) {
        val book = MockRepository.recipeBooks.find { it.id == bookId }
        if (book != null) {
            val updatedRecipes = book.recipes + recipe
            val updatedBook = book.copy(recipes = updatedRecipes)
            MockRepository.recipeBooks.remove(book)
            MockRepository.recipeBooks.add(updatedBook)
        }
    }

    fun removeRecipeFromBook(bookId: UUID, recipeId: UUID) {
        val book = MockRepository.recipeBooks.find { it.id == bookId }
        if (book != null) {
            val updatedRecipes = book.recipes.filter { it.id != recipeId }.toSet()
            val updatedBook = book.copy(recipes = updatedRecipes)
            MockRepository.recipeBooks.remove(book)
            MockRepository.recipeBooks.add(updatedBook)
        }
    }

    fun deleteRecipeBook(user: User, bookId: UUID) {
        val book = MockRepository.recipeBooks.find { it.id == bookId }
        if (book != null && book.owner == user) {
            MockRepository.recipeBooks.remove(book)
        }
    }
}
