package `is`.hi.hbv601g.h16.recipehub.persistence.dao

import androidx.room.*
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeBookEntity
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeBookRecipeCrossRef
import `is`.hi.hbv601g.h16.recipehub.persistence.RecipeEntity
import java.util.UUID

@Dao
interface RecipeBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeBooks(recipeBooks: List<RecipeBookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeBookRecipeCrossRefs(crossRefs: List<RecipeBookRecipeCrossRef>)

    @Query("SELECT * FROM recipe_books WHERE ownerId = :userId")
    suspend fun getRecipeBooksByOwner(userId: UUID): List<RecipeBookEntity>

    @Transaction
    @Query("SELECT * FROM recipes INNER JOIN recipe_book_recipe_cross_ref ON recipes.id = recipe_book_recipe_cross_ref.recipeId WHERE recipeBookId = :recipeBookId")
    suspend fun getRecipesForBook(recipeBookId: UUID): List<RecipeEntity>

    @Query("DELETE FROM recipe_book_recipe_cross_ref WHERE recipeBookId = :recipeBookId")
    suspend fun deleteCrossRefsForBook(recipeBookId: UUID)

    @Query("DELETE FROM recipe_books WHERE ownerId = :userId")
    suspend fun deleteRecipeBooksForUser(userId: UUID)
}
