package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.LikeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeBookService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.UserService
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    val recipeService: RecipeService = RecipeService(),
    val categoryService: CategoryService = CategoryService(),
    val recipeBookService: RecipeBookService = RecipeBookService(),
    val likeService: LikeService = LikeService(),
    val userService: UserService = UserService()
) : ViewModel() {

    private val likedRecipeIds = mutableStateListOf<UUID>()


    // Very basic cache for stuff we don't want to keep fetching when we don't need to
    var recipes by mutableStateOf<List<Recipe>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var recipeBooks by mutableStateOf<List<RecipeBook>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun isLiked(recipeId: UUID): Boolean = likedRecipeIds.contains(recipeId)

    fun toggleLike(recipeId: UUID) {
        viewModelScope.launch {
            if (likedRecipeIds.contains(recipeId)) {
                val result = likeService.unlikeRecipe(recipeId)
                if (result != null) {
                    likedRecipeIds.remove(recipeId)
                    // update the recipe in the list to reflect new like count
                    updateRecipeInList(result)
                }
            } else {
                val result = likeService.likeRecipe(recipeId)
                if (result != null) {
                    likedRecipeIds.add(recipeId)
                    // update the recipe in the list to reflect new like count
                    updateRecipeInList(result)
                }
            }
        }
    }

    private fun updateRecipeInList(updatedRecipe: Recipe) {
        recipes = recipes.map {
            if (it.id == updatedRecipe.id) updatedRecipe else it
        }
    }

    fun fetchRecipes() {
        viewModelScope.launch {
            isLoading = true
            recipes = recipeService.getAllRecipes(0, 10)
            isLoading = false
        }
    }

    fun fetchCategories() {
        if (categories.isNotEmpty()) return
        viewModelScope.launch {
            isLoading = true
            categories = categoryService.getAllCategories(0, 100).toList()
            isLoading = false
        }
    }

    fun fetchRecipeBooks(userId: UUID) {
        viewModelScope.launch {
            isLoading = true
            val result = recipeBookService.getByUser(userId)
            if (result != null) {
                recipeBooks = result
            }
            isLoading = false
        }
    }

    fun addRecipeToBook(bookId: UUID, recipeId: UUID) {
        viewModelScope.launch {
            isLoading = true
            val result = recipeBookService.addRecipeToBook(bookId, recipeId)
            if (result != null) {
                // Update local cache if needed, or just refresh
                val ownerId = result.owner?.id
                if (ownerId != null) {
                    fetchRecipeBooks(ownerId)
                }
            }
            isLoading = false
        }
    }

    fun createRecipeBook(name: String, isPublic: Boolean) {
        viewModelScope.launch {
            isLoading = true
            val result = recipeBookService.createRecipeBook(name, isPublic)
            if (result != null) {
                val ownerId = result.owner?.id
                if (ownerId != null) {
                    fetchRecipeBooks(ownerId)
                }
            }
            isLoading = false
        }
    }
}
