package `is`.hi.hbv601g.h16.recipehub.network.dto

data class UserRequestDTO(
    val profilePictureData: ByteArray?,
    val profilePictureType: String?,
    val bio: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserRequestDTO

        if (!profilePictureData.contentEquals(other.profilePictureData)) return false
        if (profilePictureType != other.profilePictureType) return false
        if (bio != other.bio) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profilePictureData?.contentHashCode() ?: 0
        result = 31 * result + (profilePictureType?.hashCode() ?: 0)
        result = 31 * result + (bio?.hashCode() ?: 0)
        return result
    }
}
