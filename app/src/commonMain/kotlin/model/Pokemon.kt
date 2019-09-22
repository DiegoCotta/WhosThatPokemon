package model

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    var name: String,
    var sprites: Sprite
)