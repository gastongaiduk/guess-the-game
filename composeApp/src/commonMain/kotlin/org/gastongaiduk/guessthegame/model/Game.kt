package org.gastongaiduk.guessthegame.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id:Int,
    val name:String,
    val cover:GameCover? = null,
    val screenshots:List<GameScreenshot> = emptyList()
)