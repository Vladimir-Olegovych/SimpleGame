package app.ecs.systems

import app.di.UiViewport
import app.ecs.models.Player
import app.events.GameEvent
import app.screens.game.ui.inventory.InventoryUI
import app.screens.game.ui.menu.MenuUI
import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import tools.eventbus.annotation.BusEvent

class UiSystem(): BaseSystem() {

    @Wire private lateinit var player: Player
    @Wire private lateinit var stage: Stage
    @Wire private lateinit var uiViewport: UiViewport

    @Wire private lateinit var manuUI: MenuUI
    @Wire private lateinit var inventoryUI: InventoryUI

    @BusEvent
    fun setInventory(event: GameEvent.UpdateInventory){
       if (player.entityId != event.entityId) return
        inventoryUI.updateInventory()
    }

    @BusEvent
    fun setInventory(event: GameEvent.OpenInventory){
        inventoryUI.showInventory(event.entityId)
    }

    override fun initialize() {
        val table = Table().apply { setFillParent(true) }

        manuUI.init(world, table)
        inventoryUI.init(world, table)

        inventoryUI.currentInventoryId = player.entityId

        stage.addActor(table)
    }

    override fun begin() {
        uiViewport.apply()
    }

    override fun processSystem() {
        stage.act(world.delta)
        stage.draw()
    }
}