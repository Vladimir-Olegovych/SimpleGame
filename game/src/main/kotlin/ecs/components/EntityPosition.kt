package ecs.components

import com.artemis.Component
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import kotlin.math.exp

class EntityPosition : Component() {

    private val serverPosition = Vector2(0f, 0f)
    private var currentPosition: Vector2? = null
    private val interpolationSpeed = 23.0f

    fun getServerPosition(): Vector2 = serverPosition.cpy()

    fun getInterpolatedPosition(): Vector2 {
        val currentPosition = currentPosition?: serverPosition.cpy()

        val weight = 1 - exp(-interpolationSpeed * Gdx.graphics.deltaTime)
        currentPosition.x += (serverPosition.x - currentPosition.x) * weight
        currentPosition.y += (serverPosition.y - currentPosition.y) * weight

        this.currentPosition = currentPosition
        return currentPosition.cpy()
    }

    fun setPosition(x: Float, y: Float) {
        serverPosition.x = x
        serverPosition.y = y
    }
}