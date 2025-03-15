package ecs.systems

import com.artemis.BaseSystem
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage

@All
class InputSystem: BaseSystem(), InputProcessor {

    private val cameraVelocity = Vector2()
    @Wire private lateinit var stage: Stage


    private fun setCameraVelocity(x: Float, y: Float){
        cameraVelocity.x = x
        cameraVelocity.y = y
    }

    override fun processSystem() {
        stage.camera.position.x += cameraVelocity.x
        stage.camera.position.y += cameraVelocity.y
        stage.camera.update()
    }

    override fun initialize() {}

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> setCameraVelocity(cameraVelocity.x, SPEED)
            Input.Keys.A -> setCameraVelocity(-SPEED, cameraVelocity.y)
            Input.Keys.S -> setCameraVelocity(cameraVelocity.x, -SPEED)
            Input.Keys.D -> setCameraVelocity(SPEED, cameraVelocity.y)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> setCameraVelocity(cameraVelocity.x, 0F)
            Input.Keys.A -> setCameraVelocity(0F, cameraVelocity.y)
            Input.Keys.S -> setCameraVelocity(cameraVelocity.x, 0F)
            Input.Keys.D -> setCameraVelocity(0F, cameraVelocity.y)
        }
        return false
    }

    override fun mouseMoved(x: Int, y: Int): Boolean {
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
        const val SPEED = 4F
    }
}