package app.ecs.components

import com.artemis.Component
import models.items.ItemContainer

class InventoryComponent: Component() {
    var inventorySlots: Array<Array<ItemContainer>?>? = null

    fun getSlotItems(slot: Int): Array<ItemContainer>? {
        return inventorySlots?.get(slot)
    }
}