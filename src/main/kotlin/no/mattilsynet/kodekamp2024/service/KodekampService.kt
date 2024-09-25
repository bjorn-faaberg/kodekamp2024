package no.mattilsynet.kodekamp2024.service

import no.mattilsynet.kodekamp2024.RequestData
import no.mattilsynet.kodekamp2024.ResponseData
import org.springframework.stereotype.Service

@Service
class KodekampService {
    fun behandleRequest(requestData: RequestData): ResponseData {
        return ResponseData(
            message = "Responding! from ${requestData.type} at ${requestData.location}"
        )
    }
}
