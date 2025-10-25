package org.example.ecs.processors

import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import models.TextureType
import org.example.core.eventbus.ServerEventBus
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.BodyType
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.ClientSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import tools.artemis.processor.GameProcessor
import tools.eventbus.annotation.EventCallback
import type.EntityType

class ClientProcessor(
    private val serverEventBus: ServerEventBus,
    private val clientSystem: ClientSystem,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem,
): GameProcessor {

    @EventCallback
    private fun onConnected(busEvent: BusEvent.ProcessorEvent.OnConnected) {
        clientSystem.createClient(BusEvent.CreateClient(busEvent.connection))

        val entityId = getEntityId(busEvent.connection)?: return

        entitySystem.createEntity(BusEvent.CreateEntity(
            entityId = entityId,
            textureType = TextureType.PLAYER,
            entityType = EntityType.ENTITY,
            isObserver = true,
            isStatic = false,
            isPhysical = true
        ))

        val position = Vector2(0F, 0F)

        physicsSystem.createBody(BusEvent.CreateBody(
            entityId = entityId,
            vector2 = position,
            bodyType = BodyType.CIRCLE,
            isEnabled = true
        ))

        chunkSystem.applyEntityChunk(BusEvent.ApplyEntityToChunk(
            entityId, position
        ))

    }

    @EventCallback
    private fun onDisconnected(busEvent: BusEvent.ProcessorEvent.OnDisconnected) {
        val entityId = getEntityId(busEvent.connection)?: return

        clientSystem.removeClient(BusEvent.RemoveClient(busEvent.connection))
        physicsSystem.removeBody(BusEvent.RemoveBody(entityId))
        entitySystem.removeEntity(BusEvent.RemoveEntity(entityId))
        chunkSystem.removeEntityChunk(BusEvent.RemoveEntityChunk(entityId))
    }

    @EventCallback
    private fun clientReceive(busEvent: BusEvent.ProcessorEvent.OnReceive){
        val entityId = getEntityId(busEvent.connection)?: return

        serverEventBus.sendEvent(BusEvent.OnReceiveId(
            entityId, busEvent.event
        ))
    }

    private fun getEntityId(connection: Connection): Int? {
        return clientSystem.connectionToId(connection)
    }

}