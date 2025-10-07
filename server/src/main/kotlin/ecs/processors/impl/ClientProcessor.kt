package org.example.ecs.processors.impl

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import org.example.ecs.processors.GameProcessor
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import tools.eventbus.annotation.EventCallback
import type.EntityType


class ClientProcessor(
    private val serverEventBus: ServerEventBus
): GameProcessor {

    override fun create(artemisWorld: World) {}

    @EventCallback
    private fun onConnected(busEvent: BusEvent.ProcessorEvent.OnConnected) {
        serverEventBus.sendEvent(BusEvent.CreateClient(busEvent.connection))

        val entityId = getEntityId(busEvent.connection)?: return

        serverEventBus.sendEvent(BusEvent.CreateEntity(
            entityId, EntityType.PLAYER, true
        ))

        val position = Vector2(0F, 0F)

        serverEventBus.sendEvent(BusEvent.ApplyEntityToChunk(
            entityId, position
        ))

        serverEventBus.sendEvent(BusEvent.CreateBody(
            entityId, position
        ))
    }

    @EventCallback
    private fun onDisconnected(busEvent: BusEvent.ProcessorEvent.OnDisconnected) {
        val entityId = getEntityId(busEvent.connection)?: return

        serverEventBus.sendEvent(BusEvent.RemoveClient(busEvent.connection))
        serverEventBus.sendEvent(BusEvent.RemoveBody(entityId))
        serverEventBus.sendEvent(BusEvent.RemoveEntity(entityId))
        serverEventBus.sendEvent(BusEvent.RemoveEntityChunk(entityId))
    }

    @EventCallback
    private fun clientReceive(busEvent: BusEvent.ProcessorEvent.OnReceive){
        val entityId = getEntityId(busEvent.connection)?: return

        serverEventBus.sendEvent(BusEvent.OnReceiveId(
            entityId, busEvent.event
        ))
    }

    private fun getEntityId(connection: Connection): Int? {
        return (serverEventBus.sendEvent(BusEvent.ConnectionToId(connection)) as? Int)
    }

}