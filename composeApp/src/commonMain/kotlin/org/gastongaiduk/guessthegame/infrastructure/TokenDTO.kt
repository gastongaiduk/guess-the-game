package org.gastongaiduk.guessthegame.infrastructure

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO (
    val access_token:String
)