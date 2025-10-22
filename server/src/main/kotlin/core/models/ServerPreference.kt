package org.example.core.models


data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxPlayerSpeed: Float = 1F,
    val chunkRadius: Int = 4,
    val blockSize: Float = 1F,
    val chunkSize: Float = 4F
)
