package app.ecs.utils

import app.ecs.components.InventoryComponent
import com.artemis.World
import ecs.components.ClientComponent
import event.Event
import org.example.app.ecs.components.*

fun World.utProcessCreationEvents(clientId: Int, entityId: Int) {
    val textureComponentMapper = this.getMapper(TextureComponent::class.java)
    val entityTypeComponentMapper = this.getMapper(EntityTypeComponent::class.java)
    val staticPositionComponentMapper = this.getMapper(StaticPositionComponent::class.java)
    val entityComponentMapper = this.getMapper(EntityComponent::class.java)
    val statsComponentMapper = this.getMapper(StatsComponent::class.java)
    val inventoryComponentMapper = this.getMapper(InventoryComponent::class.java)
    val physicsComponentMapper = this.getMapper(PhysicsComponent::class.java)
    val sizeComponentMapper = this.getMapper(SizeComponent::class.java)
    val clientComponentMapper = this.getMapper(ClientComponent::class.java)

    val client = clientComponentMapper[clientId]?: return
    val entity = entityComponentMapper[entityId]?: return

    val staticPositionComponent = staticPositionComponentMapper[entityId]

    client.addEvent(
        Event.Entity(
            entityId = entityId,
            drawStats = entity.drawStats,
            isStatic = staticPositionComponent != null
        )
    )

    entityTypeComponentMapper[entityId]?.let {
        client.addEvent(
            Event.EntityTypeEvent(
                entityId = entityId,
                entityType = it.entityType
            )
        )
    }

    textureComponentMapper[entityId]?.let {
        val textureContainer = it.texture?: return@let
        client.addEvent(
            Event.Texture(
                entityId = entityId,
                textureId = textureContainer.textureId
            )
        )
    }


    sizeComponentMapper[entityId]?.let {
        client.addEvent(
            Event.Size(
                entityId = entityId,
                radius = it.radius,
                halfHeight = it.halfHeight,
                halfWidth = it.halfWidth
            )
        )
    }
    staticPositionComponentMapper[entityId]?.position?.let {
        client.addEvent(
            Event.Position(
                entityId = entityId,
                x = it.x,
                y = it.y,
            )
        )
    }

    physicsComponentMapper[entityId]?.let {
        it.getBody()?: return@let
        if (staticPositionComponentMapper[entityId] == null) {
            client.addEvent(
                Event.Position(
                    entityId = entityId,
                    x = it.positionUpdater.getAll().x,
                    y = it.positionUpdater.getAll().y
                )
            )
            it.positionUpdater.markAsUpdated()
        }

        client.addEvent(
            Event.Angle(
                entityId = entityId,
                angle = it.angleUpdater.getAll()
            )
        )
        it.angleUpdater.markAsUpdated()
    }

    if (entityId == clientId) statsComponentMapper[entityId]?.let {
        it.statsUpdater.markAsUpdated()
        val stats = it.statsUpdater.getAll()
        if (stats.isEmpty()) return@let
        client.addEvent(
            Event.Stats(
                entityId = entityId,
                stats = stats,
            )
        )
    }
    if (entityId == clientId) inventoryComponentMapper[entityId]?.let {
        it.inventoryUpdater.markAsUpdated()
        val inventory = it.inventoryUpdater.getAll()
        if (inventory.isEmpty()) return@let
        client.addEvent(
            Event.Inventory(
                entityId = entityId,
                inventory = inventory,
            )
        )
    }
}