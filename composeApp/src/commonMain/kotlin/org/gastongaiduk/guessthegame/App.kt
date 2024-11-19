package org.gastongaiduk.guessthegame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import org.gastongaiduk.guessthegame.infrastructure.IGBDRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.gastongaiduk.guessthegame.model.Game

@Composable
@Preview
fun App() {
    val repository = IGBDRepository(
        BuildConfig.IGDB_CLIENT_ID,
        BuildConfig.IGDB_CLIENT_SECRET
    )

    MaterialTheme {
        var maxScore by remember { mutableStateOf(0) }
        var assertions by remember { mutableStateOf(0) }
        var gameToGuess by remember { mutableStateOf<List<Game>>(emptyList()) }
        var gamesToDistract by remember { mutableStateOf<List<Game>>(emptyList()) }
        var allGames by remember { mutableStateOf<List<Game>>(emptyList()) }
        var clicked by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
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

            LazyColumn {
                item {
                    if (allGames.size == 4) {
                        val game = gameToGuess.first()
                        var choosen by remember { mutableStateOf(Any()) }
                        if (!clicked){
                            AsyncImage(
                                model = game.screenshots.random().url
                                    .replace("//", "https://")
                                    .replace("thumb", "1080p"),
                                contentDescription = null,
                            )

                            allGames.sortedBy { it.id }.forEach {
                                Button(onClick = {
                                    clicked = true
                                    choosen = it
                                    if (choosen == game){
                                        assertions += 1
                                    } else {
                                        if (assertions > maxScore) {
                                            maxScore = assertions
                                        }
                                        assertions = 0
                                    }
                                }) {
                                    Text(it.name)
                                }
                            }
                        }

                        if (clicked) {
                            if (choosen == game) {
                                Text("Great! ${game.name} is the game of the screenshot")
                            } else {
                                Text("Bad! Correct anwser was: ${game.name}")
                            }

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

            if (assertions > 0){
                Row {
                    Text("$assertions Assertions in a row!")
                }
            }
            Row {
                Text("Max score: $maxScore")
            }
        }
    }
}