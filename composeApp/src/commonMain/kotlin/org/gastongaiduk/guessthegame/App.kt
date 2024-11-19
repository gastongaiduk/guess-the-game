package org.gastongaiduk.guessthegame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.russhwolf.settings.Settings
import org.gastongaiduk.guessthegame.infrastructure.IGBDRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.gastongaiduk.guessthegame.model.Game

const val MAX_SCORE_KEY = "maxScore"
private val settings:Settings = Settings()

@Composable
@Preview
fun App() {
    val repository = IGBDRepository(
        BuildConfig.IGDB_CLIENT_ID,
        BuildConfig.IGDB_CLIENT_SECRET
    )

    MaterialTheme {
        var maxScore = settings.getInt(MAX_SCORE_KEY, 0)
        var assertions by remember { mutableStateOf(0) }
        var gameToGuess by remember { mutableStateOf<List<Game>>(emptyList()) }
        var gamesToDistract by remember { mutableStateOf<List<Game>>(emptyList()) }
        var allGames by remember { mutableStateOf<List<Game>>(emptyList()) }
        var clicked by remember { mutableStateOf(false) }

        Column(
            Modifier.fillMaxWidth().fillMaxHeight().background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.3f))
            Row {
                Button(onClick = {
                    clicked = false
                    gameToGuess = emptyList()
                    gamesToDistract = emptyList()
                    allGames = emptyList()
                    repository.getRandomGame {
                        gameToGuess = it
                        allGames = allGames.plus(it)
                    }
                    repository.getRandomGamesJustForNames {
                        gamesToDistract = it
                        allGames = allGames.plus(it)
                    }
                }) {
                    Text("Let's play!")
                }
            }

            Spacer(Modifier.weight(1f))

            LazyColumn {
                item {
                    if (allGames.size == 4) {
                        val game = gameToGuess.first()
                        var chosen by remember { mutableStateOf(Any()) }
                        if (!clicked){
                            AsyncImage(
                                model = game.screenshots.random().url
                                    .replace("//", "https://")
                                    .replace("thumb", "1080p"),
                                contentDescription = null,
                            )

                            allGames.sortedBy { it.id }.forEach {
                                Spacer(Modifier.weight(0.1f))
                                Button(onClick = {
                                    clicked = true
                                    chosen = it
                                    if (chosen == game){
                                        assertions += 1
                                    } else {
                                        if (assertions > maxScore) {
                                            settings.putInt(MAX_SCORE_KEY, assertions)
                                            maxScore = assertions
                                        }
                                        assertions = 0
                                    }
                                }, Modifier.align(Alignment.CenterHorizontally)) {
                                    Text(it.name, textAlign = TextAlign.Center)
                                }
                            }
                        }

                        if (clicked) {
                            if (chosen == game) {
                                Text("Correct!", fontWeight = FontWeight.Bold, color = Color.Green)
                            } else {
                                Text("Incorrect!", fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                            Text(game.name, color = Color.White, fontStyle = FontStyle.Italic)

                            AsyncImage(
                                model = game.cover?.url
                                    ?.replace("//", "https://")
                                    ?.replace("thumb", "1080p"),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.weight(1f))
            if (assertions > 0){
                Row {
                    Text("Assertions in a row: $assertions", color = Color.White)
                }
            }

            Row {
                Text("Max score: $maxScore", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.weight(0.3f))
        }
    }
}