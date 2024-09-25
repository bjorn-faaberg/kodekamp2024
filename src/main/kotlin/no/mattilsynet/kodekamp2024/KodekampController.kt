package no.mattilsynet.kodekamp2024

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity

data class RequestData(
    val type: String,
    val location: String,
)

data class ResponseData(
    val message: String
)

@RestController
class KodekampController {
    @GetMapping
    fun index(): ResponseEntity<String> {
        return ResponseEntity.ok()
            .header("Content-Type", "text/html")
            .body("""
                <ul>
                    <li>POST /</li>
                    <li>GET /ping</li>
                </ul>
        """.trimIndent())
    }

    @GetMapping("/ping")
    fun ping(
        @RequestParam(value = "name", defaultValue = "World")
        name: String
    ): String {
        return "Kodekamp!";
    }

    @PostMapping("/")
    fun post(
        @RequestBody body: RequestData
    ): ResponseEntity<ResponseData> {
        val response = ResponseData(
            message = "Responding! from ${body.type} at ${body.location}"
        )

        return ResponseEntity.ok(response)
    }
}
