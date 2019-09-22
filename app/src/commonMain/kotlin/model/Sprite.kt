package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Sprite(
    @SerialName("front_default")
    var frontDefault: String? = null,

    @SerialName("back_default")
    var backDefault: String? = null
)
