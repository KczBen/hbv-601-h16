package `is`.hi.hbv601g.h16.recipehub.persistence

import android.content.Context
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.RecipeBookDao
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.RecipeDao
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.UserDao

object PersistenceModule {
    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    val userDao: UserDao
        get() = database?.userDao() ?: throw IllegalStateException("PersistenceModule not initialized")

    val recipeDao: RecipeDao
        get() = database?.recipeDao() ?: throw IllegalStateException("PersistenceModule not initialized")

    val recipeBookDao: RecipeBookDao
        get() = database?.recipeBookDao() ?: throw IllegalStateException("PersistenceModule not initialized")
}
