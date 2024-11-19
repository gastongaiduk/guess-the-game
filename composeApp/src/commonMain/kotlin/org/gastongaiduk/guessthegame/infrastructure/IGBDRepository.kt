package org.gastongaiduk.guessthegame.infrastructure

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import org.gastongaiduk.guessthegame.infrastructure.Utils.httpClient
import org.gastongaiduk.guessthegame.model.Game

const val authURL = "https://id.twitch.tv/oauth2/token"
const val gamesApiURL = "https://api.igdb.com/v4/games"

class IGBDRepository (
    private val clientId: String,
    private val clientSecret: String
) {
    private lateinit var token : String
    private var lastGameId = 0

    private suspend fun getLastGameId(): Int {
        val body = "fields id, cover.url, name, screenshots.url;" +
                "where category = 0;" +
                "sort id desc;" +
                "limit 1;"

        return withContext(Dispatchers.IO) {
            val response = httpClient.post(gamesApiURL) {
                headers.append("Authorization", "Bearer ${authenticationToken()}")
                headers.append("Client-ID", clientId)
                setBody(body)
            }.body<List<Game>>()
            response.first().id
        }
    }

    private suspend fun authenticationToken() : String
    {
        if (!this::token.isInitialized) {
            val response = httpClient
                .post("$authURL?client_id=$clientId&client_secret=$clientSecret&grant_type=client_credentials")
                .body<TokenDTO>()
            token = response.access_token
        }

        return token
    }

    private suspend fun generateRandomId() : Int
    {
        if (lastGameId == 0){
            lastGameId = getLastGameId()
        }

        return (0..lastGameId).random()
    }

    private suspend fun getGame(id: Int) : Game
    {
        val body = "fields id, cover.url, name, screenshots.url;" +
                "where id = $id;" +
                "limit 1;"

        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post(gamesApiURL) {
                    headers.append("Authorization", "Bearer ${authenticationToken()}")
                    headers.append("Client-ID", clientId)
                    setBody(body)
                }.body<List<Game>>()
                response.first()
            } catch (e: Throwable) {
                getGame(generateRandomId())
            }
        }
    }

    fun getRandomGame(onSuccessResponse: (List<Game>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            var randomGameId: Int
            var game: Game
            do{
                randomGameId = generateRandomId()
                game = getGame(randomGameId)
            } while (game.cover == null || game.screenshots.isEmpty())
            onSuccessResponse(listOf(game))
        }
    }

    fun getRandomGamesJustForNames(onSuccessResponse: (List<Game>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val startIdRange = generateRandomId()
            val endIdRange = startIdRange + 100
            val body = "fields id, name;" +
                    "where name != null & category = 0 & id >= $startIdRange & id <= $endIdRange;" +
                    "sort rating desc;" +
                    "limit 3;"
            val response = httpClient.post(gamesApiURL) {
                headers.append("Authorization", "Bearer ${authenticationToken()}")
                headers.append("Client-ID", clientId)
                setBody(body)
            }.body<List<Game>>()
            onSuccessResponse(response)
        }
    }
}