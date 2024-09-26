package no.mattilsynet.kodekamp2024.dto

data class RequestData(
    val type: String,
    val location: String,
)

data class ResponseData(
    val message: String,
)
