package org.example.app.ecs.systems

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event
import models.enums.SendType
import org.example.app.ecs.components.*

@All(ClientComponent::class)
class EventSystem: ChunkManager.Listener, IteratingSystem() {

    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>

    override fun process(entityId: Int) {
        val client = clientComponentMapper[entityId]?: return
        client.processClient(entityId)
        for (entityId in client.getEntities()) {
            client.processAllEntity(entityId)
        }
    }

    private fun ClientComponent.processClient(id: Int){
        inventoryComponentMapper[id]?.let {
            val inventory = it.fetchSendData()
            if (inventory.isEmpty()) return@let
            addEvent(
                Event.Inventory(
                    entityId = id,
                    inventory = inventory,
                )
            )
        }
    }

    private fun ClientComponent.processAllEntity(id: Int){
        staticPositionComponentMapper[id]?.let { return@processAllEntity }
        val physics = physicsComponentMapper[id]?: return
        val entityBody = physics.body?: return

        if (!entityBody.isActive || !entityBody.isAwake) return

        addEvent(
            Event.Position(
                entityId = id,
                x = entityBody.position.x,
                y = entityBody.position.y
            ),
            sendType = SendType.UDP
        )

        addEvent(
            Event.Angle(
                entityId = id,
                angle = physics.body?.angle?: 0F
            ),
            sendType = SendType.UDP
        )

        statsComponentMapper[id]?.let {
            val stats = it.fetchSendData()
            if (stats.isEmpty()) return@let
            addEvent(
                Event.Stats(
                    entityId = id,
                    stats = stats,
                )
            )
        }
    }

    private fun ClientComponent.processClientCreation(id: Int){
        inventoryComponentMapper[id]?.let {
            val inventory = it.getArrayItemContainer()
            it.fetchSendData()
            if (inventory.isEmpty()) return@let
            addEvent(
                Event.Inventory(
                    entityId = id,
                    inventory = inventory,
                )
            )
        }
    }

    private fun ClientComponent.processEntityCreation(id: Int){
        val entity = entityComponentMapper[id]?: return

        val staticPositionComponent = staticPositionComponentMapper[id]
        addEvent(
            Event.Entity(
                entityId = id,
                drawStats = entity.drawStats,
                isStatic = staticPositionComponent != null
            )
        )

        entityTypeComponentMapper[id]?.let {
            addEvent(
                Event.EntityTypeEvent(
                    entityId = id,
                    entityType = it.entityType
                )
            )
        }

        textureComponentMapper[id]?.let {
            addEvent(
                Event.Texture(
                    entityId = id,
                    textureType = it.textureType
                )
            )
        }

        sizeComponentMapper[id]?.let {
            addEvent(
                Event.Size(
                    entityId = id,
                    radius = it.radius,
                    halfHeight = it.halfHeight,
                    halfWidth = it.halfWidth
                )
            )
        }
        staticPositionComponentMapper[id]?.position?.let {
            addEvent(
                Event.Position(
                    entityId = id,
                    x = it.x,
                    y = it.y,
                )
            )
        }

        statsComponentMapper[id]?.let {
            val stats = it.getArrayStatContainer()
            it.fetchSendData()
            if (stats.isEmpty()) return@let
            addEvent(
                Event.Stats(
                    entityId = id,
                    stats = stats,
                )
            )
        }
    }


    override fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        for (activatorId in activators) {
            val client = clientComponentMapper[activatorId]?: return
            client.addEntities(entities)

            if (activatorId in entities) {
                client.processClientCreation(activatorId)
            }

            for (entityId in entities) { client.processEntityCreation(entityId) }
        }
    }

    override fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        for (activatorId in activators){
            val client = clientComponentMapper[activatorId] ?: return
            client.removeEntities(entities)
            entities.forEach { entityId ->
                client.addEvent(Event.Remove(entityId))
            }
        }

    }
}