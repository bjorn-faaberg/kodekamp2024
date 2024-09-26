package no.mattilsynet.kodekamp2024.dto

data class PlayResponse(
    val unit: String,
    val action: String,
    val x: Int,
    val y: Int
)