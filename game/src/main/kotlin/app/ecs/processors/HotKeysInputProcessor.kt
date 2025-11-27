package app.ecs.processors

import app.ecs.models.SendEvents
import com.badlogic.gdx.Input
import core.models.input.KeyInputProcessor
import event.Event

class HotKeysInputProcessor(
    private val sendEvents: SendEvents
): KeyInputProcessor() {

    private fun setCollectItems(value: Boolean){
        sendEvents.addEvent(Event.CanCollectItems(value))
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.SPACE -> setCollectItems(true)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.SPACE -> setCollectItems(false)
        }
        return false
    }

}