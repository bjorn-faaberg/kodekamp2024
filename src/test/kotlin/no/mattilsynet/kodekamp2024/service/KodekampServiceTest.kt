package no.mattilsynet.kodekamp2024.service

import no.mattilsynet.kodekamp2024.RequestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KodekampServiceTest {

    @Test
    fun testBehandleRequest() {
        val kodekampService = KodekampService()
        val requestData = RequestData("type", "location")
        val response = kodekampService.behandleRequest(requestData)
        assertThat(response.message).isEqualTo("Responding! from type at location")
    }
}
