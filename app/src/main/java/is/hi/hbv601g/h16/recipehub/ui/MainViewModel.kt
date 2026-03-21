package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.CommentService
import `is`.hi.hbv601g.h16.recipehub.domain.service.LikeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeBookService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.UserService
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    val recipeService: RecipeService = RecipeService(),
    val categoryService: CategoryService = CategoryService(),
    val recipeBookService: RecipeBookService = RecipeBookService(),
    val likeService: LikeService = LikeService(),
    val userService: UserService = UserService(),
    private val authService: AuthService = AuthService()
    val commentService: CommentService = CommentService()
) : ViewModel() {

    private val likedRecipeIds = mutableStateListOf<UUID>()


    // Very basic cache for stuff we don't want to keep fetching when we don't need to
    var recipes by mutableStateOf<List<Recipe>>(emptyList())
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var recipeBooks by mutableStateOf<List<RecipeBook>>(emptyList())
        private set

    var comments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            if (authService.tryAutoLogin()) {
                val user = AuthService.currentUser
                if (user != null) {
                    fetchRecipeBooks(user.id)
                }
            }
        }
    }

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
                val owner = result.owner
                if (owner != null) {
                    fetchRecipeBooks(owner.id)
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
                val owner = result.owner
                if (owner != null) {
                    fetchRecipeBooks(owner.id)
                }
            }
            isLoading = false
        }
    }

    fun unfollowUser(userId: UUID, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userService.unfollowUser(userId)
            // unfollowUser returns the updated User on success, or null on failure
            onResult(result != null)
        }
    }

    fun fetchComments(recipeId: UUID) {
        viewModelScope.launch {
            isLoading = true
            comments = commentService.getComments(recipeId, 0, 50)
            isLoading = false
        }
    }

    fun updateComment(recipeId: UUID, commentId: UUID, newText: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val updated = commentService.updateComment(recipeId, commentId, newText)
            if (updated != null) {
                // Replace the old comment in our local list so the UI updates instantly
                comments = comments.map { if (it.id == commentId) updated else it }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun updateRecipe(recipe: Recipe, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = recipeService.modifyRecipe(recipe)
            if (success) {
                // Update the recipe in our local list so the UI updates instantly
                recipes = recipes.map { if (it.id == recipe.id) recipe else it }
            }
            onResult(success)
        }
    }
}
