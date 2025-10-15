package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import event.Event
import tools.kyro.client.GameClient

class InputSystem: BaseSystem(), InputProcessor {

    @Wire private lateinit var gameClient: GameClient<Event>

    private val forceVector = Vector2.Zero

    private fun setForceVector(x: Float? = null, y: Float? = null){
        forceVector.x = x?: forceVector.x
        forceVector.y = y?: forceVector.y

        gameClient.sendTCP(Event.CurrentPlayerVelocity(forceVector.x, forceVector.y))
    }

    override fun processSystem() {}

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> setForceVector(y = SPEED)
            Input.Keys.A -> setForceVector(x = -SPEED)
            Input.Keys.S -> setForceVector(y = -SPEED)
            Input.Keys.D -> setForceVector(x = SPEED)
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

    override fun keyTyped(p0: Char): Boolean = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun touchCancelled(p0: Int, p1: Int, p2: Int, p3: Int): Boolean = false

    override fun touchDragged(p0: Int, p1: Int, p2: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false
    
    override fun scrolled(p0: Float, p1: Float): Boolean = false

    companion object {
        const val SPEED = 1f
    }
}
