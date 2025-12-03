package app.ecs.processors

import app.ecs.models.SendEvents
import com.badlogic.gdx.Gdx
import core.models.input.KeyInputProcessor
import event.Event
import models.network.SendType
import tools.graphics.input.SwitchInputProcessor
import kotlin.math.atan2

class LookInputProcessor(
    private val sendEvents: SendEvents
): SwitchInputProcessor() {

    private fun setAngle(value: Float){
        sendEvents.addDelayedEvent(
            delay = SEND_DELAY,
            event = Event.LookAt(value),
            sendType = SendType.UDP
        )
    }

    override fun swMouseMoved(screenX: Int, screenY: Int): Boolean {
        val centerX = Gdx.graphics.width / 2f
        val centerY = Gdx.graphics.height / 2f

        val deltaX = screenX - centerX
        val deltaY = centerY - screenY

        val angle = atan2(deltaY.toDouble(), deltaX.toDouble()).toFloat()
        setAngle(angle)
        return false
    }

    companion object {
        const val SEND_DELAY = 50L
    }
}