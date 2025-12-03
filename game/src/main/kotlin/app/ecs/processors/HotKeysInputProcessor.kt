package app.ecs.processors

import app.ecs.models.SendEvents
import app.screens.game.dialog.MenuDialog
import com.badlogic.gdx.Input
import core.models.input.KeyInputProcessor
import event.Event
import tools.graphics.input.SwitchInputProcessor
import tools.graphics.screens.dialogs.DialogManager

class HotKeysInputProcessor(
    private val sendEvents: SendEvents,
    private val menuDialog: MenuDialog,
    private val dialogManager: DialogManager
): SwitchInputProcessor() {

    private fun setCollectItems(value: Boolean){
        sendEvents.addEvent(Event.CanCollectItems(value))
    }

    override fun onDisable() {
        setCollectItems(false)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (menuDialog.isShowed()) return false
        when (keycode) {
            Input.Keys.SPACE -> setCollectItems(true)
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE){
            if(!menuDialog.isShowed()) menuDialog.show(dialogManager)
            else menuDialog.dismiss()
        }
        if (menuDialog.isShowed()) return false

        when (keycode) {
            Input.Keys.SPACE -> setCollectItems(false)
        }
        return false
    }

}