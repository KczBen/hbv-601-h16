package `is`.hi.hbv601g.h16.recipehub.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.RecipeBookDao
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.RecipeDao
import `is`.hi.hbv601g.h16.recipehub.persistence.dao.UserDao

@Database(
    entities = [
        UserEntity::class,
        RecipeEntity::class,
        RecipeBookEntity::class,
        CategoryEntity::class,
        RecipeBookRecipeCrossRef::class,
        RecipeCategoryCrossRef::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeBookDao(): RecipeBookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_hub_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
