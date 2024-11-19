package org.gastongaiduk.guessthegame.model

import kotlinx.serialization.Serializable

@Serializable
data class GameCover(
    val id:Int,
    val url:String,
)