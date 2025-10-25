package org.example.core.level.chunks

import alexey.tools.common.level.Chunk
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.eventbus.event.BusEvent
import org.example.core.level.chunks.repository.ChunkGenerator
import org.example.core.models.BodyType
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import type.EntityType

class BlockChunkGenerator(
    private val artemisWorld: World,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem
): ChunkGenerator() {

    override fun onGenerate(chunk: Chunk, position: Vector2) {
        if (random.nextInt(0, 20) > 2) return
        val entityId = artemisWorld.create()
        entitySystem.createEntity(
            BusEvent.CreateEntity(
                entityId = entityId,
                textureType = TextureType.STONE,
                entityType = EntityType.WALL,
                isObserver = false,
                isStatic = true,
                isPhysical = true,
                position = position
            ))
        physicsSystem.createBody(
            BusEvent.CreateBody(
                entityId = entityId,
                vector2 = position,
                bodyType = BodyType.SQUARE,
                isEnabled = false
            )
        )
        chunkSystem.applyEntityChunk(
            BusEvent.ApplyEntityToChunk(
                entityId, position
            )
        )
        physicsSystem.pauseBody(
            BusEvent.PauseBody(entityId)
        )
    }

}