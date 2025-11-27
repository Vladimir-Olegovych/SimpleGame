package org.example.core.models.settings

data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxPlayerSpeed: Float = 18F,
    val chunkRadius: Int = 5,
    val blockSize: Float = 1F,
    val chunkSize: Float = 4F
)