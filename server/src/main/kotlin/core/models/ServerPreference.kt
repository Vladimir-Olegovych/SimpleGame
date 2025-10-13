package org.example.core.models


data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxSpeed: Float = 0.05F,
    val chunkRadius: Int = 2,
    val blockSize: Float = 2F,
    val chunkSize: Float = 16F
)
