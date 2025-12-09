package app.processors

import app.ecs.models.GlobalAngle
import app.ecs.models.SendEvents
import com.artemis.annotations.Wire
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import event.Event
import tools.graphics.input.GameInputProcessor

class MovementInputProcessor: GlobalAngle.Listener, GameInputProcessor {

    @Wire private lateinit var sendEvents: SendEvents
    @Wire private lateinit var globalAngle: GlobalAngle

    private val forceVector = Vector2.Zero

    private var enabled = true

    override fun onResume() {
        enabled = true
    }
    override fun onPause() {
        enabled = false
        setForceVector(x = 0F, y = 0F)
    }

    private val inputVector = Vector2()

    override fun onRotate(angle: Float) {
        setForceVector(inputVector.x, inputVector.y)
    }

    private fun transformInputWithCameraAngle(inputX: Float, inputY: Float): Pair<Float, Float> {
        if (globalAngle.angle == 0f) return Pair(inputX, inputY)


        val cosAngle = MathUtils.cos(-globalAngle.angle)
        val sinAngle = MathUtils.sin(-globalAngle.angle)

        val transformedX = inputX * cosAngle - inputY * sinAngle
        val transformedY = inputX * sinAngle + inputY * cosAngle

        return Pair(transformedX, transformedY)
    }

    private fun setForceVector(x: Float? = null, y: Float? = null) {
        inputVector.x = x ?: inputVector.x
        inputVector.y = y ?: inputVector.y

        val (worldX, worldY) = transformInputWithCameraAngle(inputVector.x, inputVector.y)

        forceVector.x = worldX
        forceVector.y = worldY

        sendEvents.addDelayedEvent(
            delay = SEND_DELAY,
            event = Event.CurrentPlayerVelocity(forceVector.x, forceVector.y)
        )
    }

    override fun keyDown(keycode: Int): Boolean {
        if (!enabled) return false
        when (keycode) {
            Input.Keys.W -> setForceVector(y = VELOCITY)
            Input.Keys.A -> setForceVector(x = -VELOCITY)
            Input.Keys.S -> setForceVector(y = -VELOCITY)
            Input.Keys.D -> setForceVector(x = VELOCITY)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!enabled) return false
        when (keycode) {
            Input.Keys.W, Input.Keys.S -> setForceVector(y = 0F)
            Input.Keys.A, Input.Keys.D -> setForceVector(x = 0F)
        }
        return false
    }

    companion object {
        const val SEND_DELAY = 50L
        const val VELOCITY = 1F
    }
}