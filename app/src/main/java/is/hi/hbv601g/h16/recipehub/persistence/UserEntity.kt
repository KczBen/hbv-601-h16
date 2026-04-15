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
    val profilePictureData: ByteArray?,
    val profilePictureType: String?,
    val bio: String,
    val isBanned: Boolean,
    val isAdmin: Boolean,
    val isLoggedIn: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (isBanned != other.isBanned) return false
        if (isAdmin != other.isAdmin) return false
        if (isLoggedIn != other.isLoggedIn) return false
        if (id != other.id) return false
        if (userName != other.userName) return false
        if (email != other.email) return false
        if (passwordHash != other.passwordHash) return false
        if (!profilePictureData.contentEquals(other.profilePictureData)) return false
        if (profilePictureType != other.profilePictureType) return false
        if (bio != other.bio) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isBanned.hashCode()
        result = 31 * result + isAdmin.hashCode()
        result = 31 * result + isLoggedIn.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + passwordHash.hashCode()
        result = 31 * result + (profilePictureData?.contentHashCode() ?: 0)
        result = 31 * result + (profilePictureType?.hashCode() ?: 0)
        result = 31 * result + bio.hashCode()
        return result
    }
}
