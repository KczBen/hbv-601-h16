package `is`.hi.hbv601g.h16.recipehub.persistence.dao

import androidx.room.*
import `is`.hi.hbv601g.h16.recipehub.persistence.CategoryEntity
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeCategoryCrossRef
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeEntity
import java.util.UUID

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeCategoryCrossRefs(crossRefs: List<RecipeCategoryCrossRef>)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: UUID): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE ownerId = :userId")
    suspend fun getRecipesByOwner(userId: UUID): List<RecipeEntity>

    @Transaction
    @Query("SELECT * FROM categories INNER JOIN recipe_category_cross_ref ON categories.id = recipe_category_cross_ref.categoryId WHERE recipeId = :recipeId")
    suspend fun getCategoriesForRecipe(recipeId: UUID): List<CategoryEntity>

    @Query("DELETE FROM recipe_category_cross_ref WHERE recipeId = :recipeId")
    suspend fun deleteCategoryCrossRefsForRecipe(recipeId: UUID)
}
