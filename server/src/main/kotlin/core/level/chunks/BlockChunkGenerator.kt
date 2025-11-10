package org.example.core.level.chunks

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.level.chunks.repository.SingleChunkGenerator
import org.example.core.models.BodyType
import org.example.ecs.event.SystemEvent
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import type.EntityType

class BlockChunkGenerator(
    private val artemisWorld: World,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem
): SingleChunkGenerator() {

    override fun generatePosition(position: Vector2): Boolean {
        if (getRandom().nextInt(0, 200) > 2) return false

        val entityId = artemisWorld.create()
        entitySystem.createEntity(
            SystemEvent.CreateEntity(
                entityId = entityId,
                textureType = TextureType.STONE,
                entityType = EntityType.WALL,
                isObserver = false,
                isPhysical = true,
                staticPosition = position
            ))
        physicsSystem.createBody(
            SystemEvent.CreateBody(
                entityId = entityId,
                vector2 = position,
                bodyType = BodyType.SQUARE,
                isEnabled = false
            )
        )
        chunkSystem.applyEntityChunk(
            SystemEvent.ApplyEntityToChunk(
                entityId, position
            )
        )
        physicsSystem.pauseBody(
            SystemEvent.PauseBody(entityId)
        )
        return true
    }
}