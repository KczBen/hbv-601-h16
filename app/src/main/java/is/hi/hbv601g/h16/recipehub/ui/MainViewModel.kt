package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.LikeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeBookService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService
import `is`.hi.hbv601g.h16.recipehub.domain.service.UserService
import java.util.UUID

class MainViewModel(
    val recipeService: RecipeService = RecipeService(),
    val categoryService: CategoryService = CategoryService(),
    val recipeBookService: RecipeBookService = RecipeBookService(),
    val likeService: LikeService = LikeService(),
    val userService: UserService = UserService()
) : ViewModel() {

    private val likedRecipeIds = mutableStateListOf<UUID>()

    fun isLiked(recipeId: UUID): Boolean = likedRecipeIds.contains(recipeId)

    fun toggleLike(recipeId: UUID) {
        if (likedRecipeIds.contains(recipeId)) likedRecipeIds.remove(recipeId)
        else likedRecipeIds.add(recipeId)
    }
}
