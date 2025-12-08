package app.processors

import app.ecs.models.Player
import app.ecs.models.SendEvents
import app.screens.game.ui.dialog.MenuDialog
import app.screens.game.ui.inventory.InventoryUI
import com.artemis.annotations.Wire
import com.badlogic.gdx.Input
import event.Event
import tools.graphics.input.GameInputProcessor
import tools.graphics.screens.dialogs.DialogManager

class HotKeysInputProcessor: GameInputProcessor {

    @Wire private lateinit var dialogManager: DialogManager
    @Wire private lateinit var player: Player
    @Wire private lateinit var sendEvents: SendEvents
    @Wire private lateinit var menuDialog: MenuDialog
    @Wire private lateinit var inventoryUI: InventoryUI

    private var enabled = true
    override fun onResume() {
        enabled = true
    }
    override fun onPause() {
        enabled = false
        setCollectItems(false)
    }

    private fun setCollectItems(value: Boolean){
        sendEvents.addEvent(Event.CanCollectItems(value))
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
        if (!enabled) return false

        when (keycode) {
            Input.Keys.SPACE -> setCollectItems(false)
            Input.Keys.E -> {
                if (!inventoryUI.isInventoryWindowVisible()) inventoryUI.showInventory()
                else inventoryUI.hideInventory()
            }
        }
        return false
    }

}