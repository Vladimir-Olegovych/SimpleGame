package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class EntityPosition: Component() {
    private val interpolatedPosition = Vector2(0F, 0F)
    private val serverPosition = Vector2(0F, 0F)

    fun getServerPosition(): Vector2 = serverPosition
    fun getInterpolatedPosition(): Vector2 = serverPosition

    fun setPosition(x: Float, y: Float) {
        this.serverPosition.x = x
        this.serverPosition.y = y
    }
}