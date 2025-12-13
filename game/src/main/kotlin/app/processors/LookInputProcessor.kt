package app.processors

import app.ecs.models.SendEvents
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import event.Event
import models.network.SendType
import tools.graphics.input.GameInputProcessor
import kotlin.math.atan2

class LookInputProcessor: GameInputProcessor {

    @Wire private lateinit var sendEvents: SendEvents

    private var enabled = true
    override fun onResume() { enabled = true }
    override fun onPause() { enabled = false }

    private fun setAngle(value: Float){
        sendEvents.addDelayedEvent(
            delay = SEND_DELAY,
            event = Event.LookAt(value),
            sendType = SendType.UDP
        )
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!enabled) return false
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