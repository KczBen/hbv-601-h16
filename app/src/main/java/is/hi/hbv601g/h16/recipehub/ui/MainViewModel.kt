package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.lifecycle.ViewModel
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeBookService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService

class MainViewModel(
    val recipeService: RecipeService = RecipeService(),
    val categoryService: CategoryService = CategoryService(),
    val recipeBookService: RecipeBookService = RecipeBookService()
) : ViewModel()