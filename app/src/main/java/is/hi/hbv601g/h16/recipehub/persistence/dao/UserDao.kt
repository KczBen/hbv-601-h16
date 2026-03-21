package `is`.hi.hbv601g.h16.recipehub.persistence.dao

import androidx.room.*
import `is`.hi.hbv601g.h16.recipehub.persistence.UserEntity
import java.util.UUID

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: UUID): UserEntity?

    @Delete
    suspend fun deleteUser(user: UserEntity)
}
