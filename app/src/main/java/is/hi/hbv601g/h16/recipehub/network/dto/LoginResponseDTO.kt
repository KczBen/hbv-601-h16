package `is`.hi.hbv601g.h16.recipehub.network.dto

import com.squareup.moshi.Json
import java.util.UUID

data class LoginResponseDTO(
    @Json(name = "message") val token: String,
    val userUuid: UUID
)
