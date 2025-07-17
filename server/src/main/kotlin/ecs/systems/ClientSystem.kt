package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import model.Event
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import org.example.values.GameValues
import tools.eventbus.annotation.EventCallback
import type.EntityType

@All(Client::class)
class ClientSystem(private val scope: CoroutineScope): IteratingSystem() {

    @Wire private lateinit var serverEventBus: ServerEventBus
    private lateinit var clientMapper: ComponentMapper<Client>
    private val playersMap = HashMap<Connection, Int>()
    private val tasks = ArrayList<Deferred<Unit>>()

    @EventCallback
    private fun removeClient(busEvent: BusEvent.OnDisconnected){
        val entityId = playersMap[busEvent.connection]?: return
        val client = clientMapper[entityId]
        client.connection = null
        clientMapper.remove(entityId)
        playersMap.remove(busEvent.connection)

        serverEventBus.sendEvent(BusEvent.RemoveEntityChunk(entityId))
        serverEventBus.sendEvent(BusEvent.RemoveBody(entityId))
        serverEventBus.sendEvent(BusEvent.RemoveEntity(entityId))
    }

    @EventCallback
    private fun createClient(busEvent: BusEvent.OnConnected) {
        val entityId = world.create()
        val preference = GameValues.getServerPreference()
        playersMap[busEvent.connection] = entityId
        val client = clientMapper.create(entityId)
        client.connection = busEvent.connection

        client.addEvent(
            Event.CurrentChunkParams(
                chunkRadius = preference.chunkRadius,
                chunkSize = preference.chunkSize
            )
        )
        client.addEvent(
            Event.CurrentPlayer(
                entityId = entityId
            )
        )

        serverEventBus.sendEvent(BusEvent.CreateEntity(
            entityId, true, EntityType.PLAYER
        ))
        serverEventBus.sendEvent(BusEvent.ApplyEntityToChunk(
            entityId, Vector2(0F, 0F)
        ))
        serverEventBus.sendEvent(BusEvent.CreateBody(
            entityId, Vector2(0F, 0F)
        ))
    }
    
    @EventCallback
    private fun clientReceive(busEvent: BusEvent.OnReceive){
        val entityId = playersMap[busEvent.connection]?: return
        serverEventBus.sendEvent(BusEvent.OnReceiveId(
            entityId, busEvent.event
        ))
    }

    override fun process(entityId: Int) {

        val client = clientMapper[entityId]?: return
        client.getEvents().forEach { event ->
            tasks.add(scope.async<Unit> {
                try {
                    client.connection?.sendTCP(event)
                } catch (_: Throwable) {}
            })
        }
        client.clearEvents()
    }

    override fun end() {
        runBlocking {
            tasks.forEach { it.await() }
            tasks.clear()
        }
    }
}

