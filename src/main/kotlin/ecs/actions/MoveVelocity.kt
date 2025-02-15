package ecs.actions

import com.badlogic.gdx.math.Vector2

class MoveVelocity {

    private val velocity = Vector2()

    fun getVelocity(): Vector2 {
        return velocity
    }

    fun setVelocity(x: Float, y: Float) {
        velocity.x = x
        velocity.y = y
    }
}