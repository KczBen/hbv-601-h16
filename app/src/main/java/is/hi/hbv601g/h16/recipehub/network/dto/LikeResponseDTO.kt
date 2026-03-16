package `is`.hi.hbv601g.h16.recipehub.network.dto

import java.util.UUID

data class LikeResponseDTO(
    val id: UUID,
    val ownerUuid: UUID
)
