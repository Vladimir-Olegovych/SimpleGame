package org.example.app.ecs.systems

import alexey.tools.server.level.AdvancedChunkManager
import app.ecs.components.InventoryComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.app.ecs.components.ContactItemsComponent
import org.example.app.ecs.components.ItemComponent
import org.example.app.ecs.components.PhysicsComponent
import org.example.app.ecs.utils.utRemoveBody

@All(InventoryComponent::class)
class CollectItemsSystem: IteratingSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var contactItemsComponentMapper: ComponentMapper<ContactItemsComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var itemComponentMapper: ComponentMapper<ItemComponent>

    override fun process(entityId: Int) {
        val inventory = inventoryComponentMapper[entityId]?: return
        val contactItemsComponent = contactItemsComponentMapper[entityId]?: return
        if (!inventory.canCollectItems) return
        for (itemId in contactItemsComponent.getItems()) { inventory.collectItem(itemId) }
        contactItemsComponent.clearItems()
    }

    private fun InventoryComponent.collectItem(itemId: Int){
        val item = itemComponentMapper[itemId]?: return
        if(!this.addItem(item.worldItem?: return)) return
        world.utRemoveBody(itemId)
        chunkManager.remove(itemId)
    }
}