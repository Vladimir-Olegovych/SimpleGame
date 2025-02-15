package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import ecs.actions.MoveVelocity

@All
class InputSystem: BaseSystem(), InputProcessor {

    @Wire private lateinit var moveVelocity: MoveVelocity

    override fun processSystem() {}

    override fun initialize() {}

    override fun keyDown(keycode: Int): Boolean {
        val velocity = moveVelocity.getVelocity()
        when (keycode) {
            Input.Keys.W -> moveVelocity.setVelocity(velocity.x, SPEED)
            Input.Keys.A -> moveVelocity.setVelocity(-SPEED, velocity.y)
            Input.Keys.S -> moveVelocity.setVelocity(velocity.x, -SPEED)
            Input.Keys.D -> moveVelocity.setVelocity(SPEED, velocity.y)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        val velocity = moveVelocity.getVelocity()
        when (keycode) {
            Input.Keys.W -> moveVelocity.setVelocity(velocity.x, 0F)
            Input.Keys.A -> moveVelocity.setVelocity(0F, velocity.y)
            Input.Keys.S -> moveVelocity.setVelocity(velocity.x, 0F)
            Input.Keys.D -> moveVelocity.setVelocity(0F, velocity.y)
        }
        return false
    }

    override fun mouseMoved(x: Int, y: Int): Boolean {
        val newX = when {
            x <= EDGE_MARGIN -> -SPEED
            x >= Gdx.graphics.width - EDGE_MARGIN -> SPEED
            else -> 0F
        }

        val newY = when {
            y <= EDGE_MARGIN -> SPEED
            y >= Gdx.graphics.height - EDGE_MARGIN -> -SPEED
            else -> 0F
        }
        moveVelocity.setVelocity(newX, newY)
        return false
    }

    override fun keyTyped(p0: Char): Boolean {
        return false
    }

    override fun touchDown(p0: Int, p1: Int, p2: Int, p3: Int): Boolean {
        return false
    }

    override fun touchUp(p0: Int, p1: Int, p2: Int, p3: Int): Boolean {
        return false
    }

    override fun touchCancelled(p0: Int, p1: Int, p2: Int, p3: Int): Boolean {
        return false
    }

    override fun touchDragged(p0: Int, p1: Int, p2: Int): Boolean {
        return false
    }

    override fun scrolled(p0: Float, p1: Float): Boolean {
        return false
    }

    companion object {
        const val SPEED = 25F
        const val EDGE_MARGIN = 50
    }
}