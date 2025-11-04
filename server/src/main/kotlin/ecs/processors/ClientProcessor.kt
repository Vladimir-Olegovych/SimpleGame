package org.example.ecs.processors

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.kryonet.Connection
import event.Event
import models.TextureType
import org.example.core.models.BodyType
import values.ApplicationValues
import org.example.ecs.event.SystemEvent
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.ClientSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import tools.artemis.processor.GameProcessor
import tools.kyro.common.GameNetworkListener
import type.EntityType
import java.util.HashMap
import javax.inject.Inject

class ClientProcessor(
    private val clientSystem: ClientSystem,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem,
): GameProcessor, GameNetworkListener<Event> {

    @Inject lateinit var artemisWorld: World
    private val playersMap = HashMap<Connection, Int>()

    private val playerStats = mapOf<String, Any>(
        ApplicationValues.Stats.HP to 100
    )

    override fun onConnected(connection: Connection) {
        val entityId = artemisWorld.create()
        playersMap[connection] = entityId

        clientSystem.createClient(SystemEvent.CreateClient(
            entityId = entityId,
            connection = connection
        ))

        entitySystem.createEntity(SystemEvent.CreateEntity(
            entityId = entityId,
            textureType = TextureType.PLAYER,
            entityType = EntityType.ENTITY,
            isObserver = true,
            isPhysical = true,
            entityStats = playerStats
        ))

        val position = Vector2(0F, 0F)

        physicsSystem.createBody(SystemEvent.CreateBody(
            entityId = entityId,
            vector2 = position,
            bodyType = BodyType.CIRCLE,
            angularDamping = 10F,
            linearDamping = 10F,
            isEnabled = true
        ))

        chunkSystem.applyEntityChunk(SystemEvent.ApplyEntityToChunk(
            entityId, position
        ))
    }

    override fun onDisconnected(connection: Connection) {
        val entityId = playersMap[connection]?: return

        clientSystem.removeClient(SystemEvent.RemoveClient(entityId))
        physicsSystem.removeBody(SystemEvent.RemoveBody(entityId))
        entitySystem.removeEntity(SystemEvent.RemoveEntity(entityId))
        chunkSystem.removeEntityChunk(SystemEvent.RemoveEntityChunk(entityId))

        playersMap.remove(connection)
    }

    inner class ClientProcessorContent {
        fun getPlayers(): Map<Connection, Int> {
            return playersMap
        }
    }

}