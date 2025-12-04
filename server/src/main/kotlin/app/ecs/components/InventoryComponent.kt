package org.example.app.ecs.components

import app.ecs.components.sender.UpdatableData
import com.artemis.Component
import models.items.ItemContainer
import org.example.core.items.WorldItem
import java.util.concurrent.ConcurrentLinkedQueue

class InventoryComponent: Component() {

    var canCollectItems = false

    var capacity: Int = 1024 / 4
    private var hasUpdate = false
    private val inventoryQueue = ConcurrentLinkedQueue<WorldItem>()

    fun addItem(worldItem: WorldItem): Boolean {
        if (inventoryQueue.size >= capacity) return false
        if (!canCollectItems) return false
        hasUpdate = true
        inventoryQueue.add(worldItem)
        return true
    }

    fun removeItem(worldItem: WorldItem){
        hasUpdate = true
        inventoryQueue.remove(worldItem)
    }

    val inventoryUpdater = object : UpdatableData<Array<ItemContainer>>() {
        override fun onHasUpdate(): Boolean {
            return hasUpdate
        }

        override fun onMarkAsUpdated() {
            hasUpdate = false
        }

        override fun getUpdate(): Array<ItemContainer> {
            return inventoryQueue.map {
                ItemContainer(
                    entityId = it.entityId,
                    worldId = it.worldId,
                    name = it.name,
                    description = it.description
                )
            }.toTypedArray()
        }

        override fun getAll(): Array<ItemContainer> {
            return getUpdate()
        }
    }
}