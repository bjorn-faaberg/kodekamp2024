package no.mattilsynet.kodekamp2024

import no.mattilsynet.kodekamp2024.service.KodekampService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class RequestData(
    val type: String,
    val location: String,
)

data class ResponseData(
    val message: String,
)

@RestController
class KodekampController(
    private val kodekampService: KodekampService,
) {
    @GetMapping
    fun index(): ResponseEntity<String> {
        return ResponseEntity.ok()
            .header("Content-Type", "text/html")
            .body(
                """
                <ul>
                    <li>POST /</li>
                    <li>GET /ping</li>
                </ul>
        """.trimIndent()
            )
    }

    @GetMapping("/ping")
    fun ping(
        @RequestParam(value = "name", defaultValue = "World")
        name: String,
    ): String {
        return "Kodekamp!";
    }

    @PostMapping("/")
    fun post(
        @RequestBody body: RequestData,
    ): ResponseEntity<ResponseData> =
        kodekampService.behandleRequest(body)
            .let { ResponseEntity.ok().body(it) }
}
