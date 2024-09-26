package no.mattilsynet.kodekamp2024.service

import no.mattilsynet.kodekamp2024.dto.GameState
import no.mattilsynet.kodekamp2024.dto.PlayResponse
import no.mattilsynet.kodekamp2024.dto.RequestData
import no.mattilsynet.kodekamp2024.dto.ResponseData
import org.springframework.stereotype.Service

@Service
class KodekampService {
    fun behandleRequest(state: GameState) = listOf(PlayResponse(
        unit = "test",
        action = "action",
        x = 1,
        y = 1
    ))
}
