package `is`.hi.hbv601g.h16.recipehub.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.UUID

object NetworkModule {
    private const val BASE_URL = "https://hbv-501-h24.onrender.com/"

    private val uuidAdapter = object : JsonAdapter<UUID>() {
        override fun fromJson(reader: JsonReader): UUID? {
            return UUID.fromString(reader.nextString())
        }

        override fun toJson(writer: JsonWriter, value: UUID?) {
            writer.value(value?.toString())
        }
    }

    private val moshi = Moshi.Builder()
        .add(UUID::class.java, uuidAdapter)
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val apiService: RecipeHubApi by lazy {
        retrofit.create(RecipeHubApi::class.java)
    }
}
