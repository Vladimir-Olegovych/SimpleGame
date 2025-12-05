package org.example.app.ecs.systems

import alexey.tools.server.level.AdvancedChunkManager
import app.ecs.components.InventoryComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import core.models.components.texture.TextureContainer
import ecs.components.ClientComponent
import event.Event
import event.GamePacket
import models.entity.EntityType
import models.network.SendType
import models.textures.TextureType
import org.example.app.ecs.components.*
import org.example.app.ecs.utils.*
import org.example.core.models.box2d.BodyType
import org.example.core.models.server.EventContainer
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener
import values.ApplicationValues
import java.util.*
import kotlin.reflect.KClass

@All(ClientComponent::class)
class ClientSystem: GameNetworkListener<GamePacket>, IteratingSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager
    @Wire private lateinit var eventBus: EventBus

    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    private val playersMap = HashMap<Connection, Int>()
    private val connectedUsers = LinkedList<Connection>()
    private val disconnectedUsers = LinkedList<Connection>()

    private val worldSpawnPoint = Vector2(0F, 0F)
    private fun spawnPlayerStats() = mapOf<String, Any>(
        ApplicationValues.Stats.HP to 100
    )

    override fun dispose() {
        eventBus.clear()
    }
    
    override fun onReceive(connection: Connection, value: GamePacket) {
        val entityId = playersMap[connection]?: return
        val events = value.events
        for(event in events) {
            val container = EventContainer(entityId, event)
            val customType: KClass<out Event> = event::class
            eventBus.sendEvent(container, customType)
        }
    }

    override fun onConnected(connection: Connection) {
        connectedUsers.add(connection)
    }

    override fun onDisconnected(connection: Connection) {
        disconnectedUsers.add(connection)
    }

    override fun begin() {
        val cuIterator = connectedUsers.iterator()
        while(cuIterator.hasNext()) {
            val connection = cuIterator.next()
            val entityId = world.create()

            playersMap[connection] = entityId
            world.utCreateClient(entityId, connection)
            world.utCreateEntity(
                entityId = entityId,
                texture = TextureContainer.get(TextureType.ENTITY.PLAYER),
                entityType = EntityType.ENTITY,
                isObserver = true,
                isPhysical = true,
                canCollectItems = false,
                hasInventory = true,
                entityStats = spawnPlayerStats(),
            )
            world.utCreateBody(
                entityId = entityId,
                vector2 = worldSpawnPoint,
                bodyType = BodyType.CIRCLE,
                angularDamping = 10F,
                linearDamping = 10F,
                isEnabled = true
            )

            val chunk = chunkManager.obtainChunk(worldSpawnPoint)
            val entityComponent = entityComponentMapper[entityId]
            chunk.add(entityId, entityComponent.isObserver)

            cuIterator.remove()
        }

        val duIterator = disconnectedUsers.iterator()
        while(duIterator.hasNext()){
            val connection = duIterator.next()
            val entityId = playersMap[connection]?: continue

            world.utRemoveClient(entityId)
            world.utRemoveBody(entityId)
            world.utRemoveEntity(entityId)
            chunkManager.remove(entityId)
            playersMap.remove(connection)

            duIterator.remove()
        }
    }

    override fun process(clientId: Int) {
        val client = clientComponentMapper[clientId]?: return
        val entities = client.getEntities()

        for (entityId in entities) {
            val entity = entityComponentMapper[entityId]?: continue
            val physics = physicsComponentMapper[entityId] ?: continue
            val entityBody = physics.getBody() ?: continue

            if (entityBody.isActive || entityBody.isAwake) physics.let {
                if (staticPositionComponentMapper[entityId] == null && it.positionUpdater.hasUpdate()) {
                    client.addEvent(
                        Event.Position(
                            entityId = entityId,
                            x = it.positionUpdater.getUpdate().x,
                            y = it.positionUpdater.getUpdate().y
                        ),
                        sendType = SendType.UDP
                    )
                    it.positionUpdater.markAsUpdated()
                }

                if (it.angleUpdater.hasUpdate()) {
                    client.addEvent(
                        Event.Angle(
                            entityId = entityId,
                            angle = it.angleUpdater.getUpdate()
                        ),
                        sendType = SendType.UDP
                    )
                    it.angleUpdater.markAsUpdated()
                }
            }

            if (entityId == clientId) statsComponentMapper[entityId]?.let {
                if (!it.statsUpdater.hasUpdate()) return@let
                val stats = it.statsUpdater.getUpdate()
                it.statsUpdater.markAsUpdated()
                if (stats.isEmpty()) return@let
                client.addEvent(
                    Event.Stats(
                        entityId = entityId,
                        stats = stats,
                    )
                )
            }

            if (entityId == clientId) inventoryComponentMapper[entityId]?.let {
                if (!it.inventoryUpdater.hasUpdate()) return@let
                val inventory = it.inventoryUpdater.getUpdate()
                it.inventoryUpdater.markAsUpdated()
                if (inventory.isEmpty()) return@let
                client.addEvent(
                    Event.Inventory(
                        entityId = entityId,
                        inventory = inventory,
                    )
                )
            }
        }
    }

    override fun end() {
        for (i in 0 until subscription.entities.size()) {
            val clientId = subscription.entities[i]
            val client = clientComponentMapper[clientId]?: continue
            val entities = client.getEntities()

            fun finishProcess(entityId: Int) {
                physicsComponentMapper[entityId]?.let {
                    it.positionUpdater.finishUpdate()
                    it.angleUpdater.finishUpdate()
                }
                statsComponentMapper[entityId]?.statsUpdater?.finishUpdate()
                inventoryComponentMapper[entityId]?.inventoryUpdater?.finishUpdate()
            }

            entities.forEach { finishProcess(it) }
        }
    }

}