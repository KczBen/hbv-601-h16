package `is`.hi.hbv601g.h16.recipehub.network.dto

data class CommentRequestDTO(
    val textContent: String,
    val imageData: List<String> = emptyList(),
    val imageType: List<String> = emptyList()
)

