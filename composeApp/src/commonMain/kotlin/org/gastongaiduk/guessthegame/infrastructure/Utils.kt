package org.gastongaiduk.guessthegame.infrastructure

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object Utils {
    val httpClient = HttpClient {
        install(ContentNegotiation){
            json(json = Json { ignoreUnknownKeys = true; encodeDefaults = false }, contentType = ContentType.Any)
        }
    }
}