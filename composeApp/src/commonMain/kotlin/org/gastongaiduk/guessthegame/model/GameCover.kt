package org.gastongaiduk.guessthegame.model

import kotlinx.serialization.Serializable

@Serializable
data class GameScreenshot(
    val id:Int,
    val url:String,
)