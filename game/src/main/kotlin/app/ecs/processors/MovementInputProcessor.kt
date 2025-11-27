package app.ecs.processors

import app.ecs.models.SendEvents
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import core.models.input.KeyInputProcessor
import event.Event

class MovementInputProcessor(
    private val sendEvents: SendEvents
): KeyInputProcessor() {

    private val forceVector = Vector2.Zero

    private fun setForceVector(x: Float? = null, y: Float? = null){
        forceVector.x = x?: forceVector.x
        forceVector.y = y?: forceVector.y
        sendEvents.addEvent(Event.CurrentPlayerVelocity(forceVector.x, forceVector.y))
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> setForceVector(y = VELOCITY)
            Input.Keys.A -> setForceVector(x = -VELOCITY)
            Input.Keys.S -> setForceVector(y = -VELOCITY)
            Input.Keys.D -> setForceVector(x = VELOCITY)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W, Input.Keys.S -> setForceVector(y = 0F)
            Input.Keys.A, Input.Keys.D -> setForceVector(x = 0F)
        }
        return false
    }

    companion object {
        const val VELOCITY = 1F
    }
}