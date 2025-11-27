package org.example.app.ecs.systems

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.components.InventoryComponent
import org.example.app.ecs.components.ItemComponent
import org.example.app.ecs.components.PhysicsComponent
import org.example.app.ecs.utils.utRemoveBody

@All(EntityComponent::class)
class CollectItemsSystem: IteratingSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var itemComponentMapper: ComponentMapper<ItemComponent>

    override fun process(entityId: Int) {
        val inventory = inventoryComponentMapper[entityId]?: return
        if (!inventory.canCollectItems) return
        for (itemId in inventory.potentialCollectItems) { inventory.collectItem(itemId) }
        inventory.potentialCollectItems.clear()
    }

    private fun InventoryComponent.collectItem(itemId: Int){
        val item = itemComponentMapper[itemId]?: return
        this.addItem(item.worldItem?: return)
        world.utRemoveBody(itemId)
        chunkManager.remove(itemId)
    }
}