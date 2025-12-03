package org.example.app.ecs.components

import com.artemis.Component
import models.items.ItemContainer
import org.example.app.ecs.components.sender.CSender
import org.example.core.items.WorldItem
import java.util.concurrent.ConcurrentLinkedQueue

class InventoryComponent: CSender<Array<ItemContainer>>, Component() {

    var canCollectItems = false

    private var hasNewData = false
    private val inventoryQueue = ConcurrentLinkedQueue<WorldItem>()

    fun addItem(worldItem: WorldItem){
        hasNewData = true
        inventoryQueue.add(worldItem)
    }

    fun removeItem(worldItem: WorldItem){
        hasNewData = true
        inventoryQueue.remove(worldItem)
    }

    fun getItems(): Array<WorldItem> = inventoryQueue.toTypedArray()

    fun getArrayItemContainer() = inventoryQueue.map {
        ItemContainer(
            entityId = it.entityId,
            worldId = it.worldId,
            name = it.name,
            description = it.description
        )
    }.toTypedArray()

    override fun fetchSendData(): Array<ItemContainer> {
        return if (hasNewData) {
            hasNewData = false
            inventoryQueue.map {
                ItemContainer(
                    entityId = it.entityId,
                    worldId = it.worldId,
                    name = it.name,
                    description = it.description
                )
            }.toTypedArray()
        } else {
            arrayOf()
        }
    }

}