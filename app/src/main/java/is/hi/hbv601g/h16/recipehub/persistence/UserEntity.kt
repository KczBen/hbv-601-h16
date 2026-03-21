package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: UUID,
    val userName: String,
    val email: String,
    val passwordHash: String,
    val profilePictureURL: String,
    val bio: String,
    val isBanned: Boolean,
    val isAdmin: Boolean,
    val isLoggedIn: Boolean
)
