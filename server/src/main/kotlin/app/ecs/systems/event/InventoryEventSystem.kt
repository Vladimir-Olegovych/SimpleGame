package app.ecs.systems.event

import app.ecs.components.InventoryComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event

@All(ClientComponent::class, InventoryComponent::class)
class InventoryEventSystem: IteratingSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>

    override fun inserted(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        val inventoryComponent = inventoryComponentMapper[entityId]?: return
        inventoryComponent.inventoryUpdater.markAsUpdated()
        val inventory = inventoryComponent.inventoryUpdater.getAll()
        clientComponent.addEvent(
            Event.Inventory(
                entityId = entityId,
                inventory = inventory,
            )
        )
    }

    override fun process(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        val inventoryComponent = inventoryComponentMapper[entityId]?: return

        if (!inventoryComponent.inventoryUpdater.hasUpdate()) return
        val inventory = inventoryComponent.inventoryUpdater.getUpdate()
        if (inventory.isEmpty()) return
        clientComponent.addEvent(
            Event.Inventory(
                entityId = entityId,
                inventory = inventory,
            )
        )
        inventoryComponent.inventoryUpdater.markAsUpdated()
    }

}