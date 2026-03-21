package `is`.hi.hbv601g.h16.recipehub.persistence

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime
import java.util.UUID

class Converters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val setStringAdapter = moshi.adapter<Set<String>>(
        Types.newParameterizedType(Set::class.java, String::class.java)
    )

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromUUID(value: String?): UUID? {
        return value?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun uuidToString(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromStringSet(value: String?): Set<String>? {
        return value?.let { setStringAdapter.fromJson(it) }
    }

    @TypeConverter
    fun stringSetToString(set: Set<String>?): String? {
        return setStringAdapter.toJson(set)
    }
}
