package app.ecs.processors

import app.ecs.models.Player
import app.ecs.models.SendEvents
import app.events.GameEvent
import app.screens.game.ui.dialog.MenuDialog
import com.artemis.annotations.Wire
import com.badlogic.gdx.Input
import event.Event
import tools.eventbus.EventBus
import tools.graphics.input.SwitchInputProcessor
import tools.graphics.screens.dialogs.DialogManager

class HotKeysInputProcessor: SwitchInputProcessor() {

    @Wire private lateinit var eventBus: EventBus
    @Wire private lateinit var player: Player
    @Wire private lateinit var sendEvents: SendEvents
    @Wire private lateinit var menuDialog: MenuDialog
    @Wire private lateinit var dialogManager: DialogManager

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
            Input.Keys.E -> eventBus.sendEvent(GameEvent.OpenInventory(player.entityId))
        }
        return false
    }

}