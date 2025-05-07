package org.example.values

import com.esotericsoftware.kryonet.Connection

object GameValues {
    val playersMap = HashMap<Connection, Int>()
    const val SENSOR_RADIUS = 1F
    const val MAX_SPEED = 0.05F
}