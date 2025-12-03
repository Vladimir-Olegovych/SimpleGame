package org.example.app.ecs.systems

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import core.models.components.texture.TextureContainer
import event.Event
import event.GamePacket
import models.entity.EntityType
import models.textures.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.utils.*
import org.example.core.items.manager.ItemsManager
import org.example.core.models.box2d.BodyType
import org.example.core.models.server.EventContainer
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener
import values.ApplicationValues
import java.util.*
import kotlin.reflect.KClass

class ClientSystem: GameNetworkListener<GamePacket>, BaseSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager
    @Wire private lateinit var itemsManager: ItemsManager
    @Wire private lateinit var eventBus: EventBus

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

    override fun processSystem() {
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
                /*
                inventoryItems = listOf(
                    itemsManager.create(GunItem::class.java),
                    itemsManager.create(FoodItem::class.java),
                )

                 */
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
}