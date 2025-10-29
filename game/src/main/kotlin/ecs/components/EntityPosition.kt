package ecs.components

import com.artemis.Component
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import kotlin.math.exp

class EntityPosition : Component() {

    private var hasPosition = false
    private val serverPosition = Vector2(0f, 0f)
    private val currentPosition = Vector2(0f, 0f)
    private val interpolationSpeed = 20.0f

    fun hasPosition(): Boolean = hasPosition

    fun getServerPosition(): Vector2 = serverPosition.cpy()

    fun getInterpolatedPosition(): Vector2 {
        val weight = 1 - exp(-interpolationSpeed * Gdx.graphics.deltaTime)
        currentPosition.x += (serverPosition.x - currentPosition.x) * weight
        currentPosition.y += (serverPosition.y - currentPosition.y) * weight
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