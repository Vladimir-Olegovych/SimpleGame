package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class EntityPosition : Component() {

    private var hasPosition = false
    private val serverPosition = Vector2(0f, 0f)
    private val currentPosition = Vector2(0f, 0f)
    private val interpolationFactor = 0.3f

    fun hasPosition(): Boolean = hasPosition

    fun getServerPosition(): Vector2 = serverPosition.cpy()

    fun getInterpolatedPosition(): Vector2 {
        currentPosition.x += (serverPosition.x - currentPosition.x) * interpolationFactor
        currentPosition.y += (serverPosition.y - currentPosition.y) * interpolationFactor
        return currentPosition.cpy()
    }

    fun setPosition(x: Float, y: Float) {
        serverPosition.x = x
        serverPosition.y = y
        if (hasPosition) return
        currentPosition.x = x
        currentPosition.y = y
        hasPosition = true
    }
}