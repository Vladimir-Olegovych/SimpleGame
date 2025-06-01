package org.example.models

import com.badlogic.gdx.math.Vector2

data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxSpeed: Float = 0.05F,
    val chunkRadius: Int = 2,
    val chunkSize: SavedVector2 = SavedVector2(2F, 2F)
)

data class SavedVector2(val x: Float = 0F, val y: Float = 0F) {
    fun toVector2(): Vector2 = Vector2(x, y)
}