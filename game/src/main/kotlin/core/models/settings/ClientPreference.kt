package core.models.settings

import com.badlogic.gdx.graphics.Color

data class ClientPreference(
    var playerColor: Int = Color.RED.toIntBits(),
)