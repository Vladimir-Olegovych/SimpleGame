package app.processors

import alexey.tools.common.math.IntVector2
import app.ecs.models.GlobalAngle
import app.ecs.models.SendEvents
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import event.Event
import models.network.SendType
import tools.graphics.input.GameInputProcessor
import kotlin.math.atan2

class LookInputProcessor: GlobalAngle.Listener, GameInputProcessor {

    @Wire private lateinit var globalAngle: GlobalAngle
    @Wire private lateinit var sendEvents: SendEvents

    private var enabled = true
    override fun onResume() { enabled = true }
    override fun onPause() { enabled = false }

    private val inputVector = IntVector2(0, 0)

    override fun onRotate(angle: Float) {
        val angle = transformInputWithCameraAngle(inputVector.x, inputVector.y)
        setAngle(angle)
    }

    private fun setAngle(value: Float){
        sendEvents.addDelayedEvent(
            delay = SEND_DELAY,
            event = Event.LookAt(value),
            sendType = SendType.UDP
        )
    }

    private fun transformInputWithCameraAngle(screenX: Int, screenY: Int): Float {
        val centerX = Gdx.graphics.width / 2f
        val centerY = Gdx.graphics.height / 2f

        val deltaX = screenX - centerX
        val deltaY = centerY - screenY

        val cosAngle = MathUtils.cos(-globalAngle.angle)
        val sinAngle = MathUtils.sin(-globalAngle.angle)

        val transformedX = deltaX * cosAngle - deltaY * sinAngle
        val transformedY = deltaX * sinAngle + deltaY * cosAngle

        return atan2(transformedY.toDouble(), transformedX.toDouble()).toFloat()
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!enabled) return false
        inputVector.x = screenX
        inputVector.y = screenY

        val angle = transformInputWithCameraAngle(inputVector.x, inputVector.y)
        setAngle(angle)
        return false
    }

    companion object {
        const val SEND_DELAY = 50L
    }
}