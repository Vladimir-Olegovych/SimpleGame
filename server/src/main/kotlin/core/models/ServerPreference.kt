package org.example.core.models

import model.Vector2Data

data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxSpeed: Float = 0.05F,
    val chunkRadius: Int = 2,
    val blockSize: Float = 2F,
    val chunkSize: Vector2Data = Vector2Data(4F, 4F)
)
