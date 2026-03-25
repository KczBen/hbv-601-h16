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
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    val recipeService: RecipeService = RecipeService(),
    val categoryService: CategoryService = CategoryService(),
    val recipeBookService: RecipeBookService = RecipeBookService(),
    val likeService: LikeService = LikeService(),
    val userService: UserService = UserService(),
    private val authService: AuthService = AuthService(),
    val commentService: CommentService = CommentService()
) : ViewModel() {

    private val likedRecipeIds = mutableStateListOf<UUID>()
    private val userCache = mutableMapOf<UUID, User>()


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
            val fetched = recipeService.getAllRecipes(0, 10)
            recipes = fetched
            
            val enriched = enrichRecipesList(fetched)
            recipes = enriched
            isLoading = false
        }
    }

    private suspend fun enrichRecipesList(list: List<Recipe>): List<Recipe> = coroutineScope {
        val ownerIds = list.map { it.owner.id }.distinct()
        val owners = ownerIds.map { id -> 
            async { id to fetchUserWithCache(id) }
        }.awaitAll().toMap()
        
        list.map { recipe ->
            owners[recipe.owner.id]?.let { recipe.copy(owner = it) } ?: recipe
        }
    }

    private suspend fun fetchUserWithCache(userId: UUID): User? {
        userCache[userId]?.let { return it }
        val user = userService.getUser(userId)
        if (user != null) {
            userCache[userId] = user
        }
        return user
    }

    private fun updateUserInCacheAndLists(user: User) {
        userCache[user.id] = user
        // update recipes owned by this user
        recipes = recipes.map {
            if (it.owner.id == user.id) it.copy(owner = user) else it
        }
        // update comments owned by this user
        comments = comments.map {
            if (it.owner.id == user.id) it.copy(owner = user) else it
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
                val enriched = coroutineScope {
                    result.map { book ->
                        async {
                            val owner = book.owner?.id?.let { fetchUserWithCache(it) }
                            if (owner != null) book.copy(owner = owner) else book
                        }
                    }.awaitAll()
                }
                recipeBooks = enriched
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

    fun followUser(userId: UUID, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val result = userService.followUser(userId)
            if (result != null) {
                // 'result' is the current user (me) with updated 'following' list
                refreshFollowData(result, userId, onResult)
            } else {
                onResult(null)
            }
        }
    }

    fun unfollowUser(userId: UUID, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val result = userService.unfollowUser(userId)
            if (result != null) {
                // 'result' is the current user (me) with updated 'following' list
                refreshFollowData(result, userId, onResult)
            } else {
                onResult(null)
            }
        }
    }

    private suspend fun refreshFollowData(updatedMe: User, targetUserId: UUID, onResult: (User?) -> Unit) {
        // Update current user state with the returned object (which is 'me')
        AuthService.currentUser = updatedMe
        updateUserInCacheAndLists(updatedMe)

        // Fetch the target user (the one we followed/unfollowed) to get their updated follower count
        val updatedTarget = userService.getUser(targetUserId)
        if (updatedTarget != null) {
            updateUserInCacheAndLists(updatedTarget)
        }

        onResult(updatedTarget)
    }

    fun fetchComments(recipeId: UUID) {
        viewModelScope.launch {
            isLoading = true
            val fetched = commentService.getComments(recipeId, 0, 50)
            comments = fetched
            
            val enriched = coroutineScope {
                fetched.map { comment ->
                    async {
                        val user = fetchUserWithCache(comment.owner.id)
                        if (user != null) comment.copy(owner = user) else comment
                    }
                }.awaitAll()
            }
            comments = enriched
            isLoading = false
        }
    }

    fun createComment(recipeId: UUID, textContent: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val created = commentService.createComment(recipeId, textContent)
            if (created != null) {
                // Refresh comments to include the new one (and ensure it's enriched with owner info)
                fetchComments(recipeId)
                onResult(true)
            } else {
                onResult(false)
            }
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

    fun logout() {
        viewModelScope.launch {
            authService.logout()
            recipeBooks = emptyList()
            likedRecipeIds.clear()
            userCache.clear()
        }
    }
}
