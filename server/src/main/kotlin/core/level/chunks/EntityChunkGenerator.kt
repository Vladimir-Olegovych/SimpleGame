package org.example.core.level.chunks

import alexey.tools.common.level.Chunk
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.level.chunks.repository.ChunkGenerator
import org.example.core.models.BodyType
import values.ApplicationValues
import org.example.ecs.event.SystemEvent
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import type.EntityType
import kotlin.Any
import kotlin.String

class EntityChunkGenerator(
    private val artemisWorld: World,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem
): ChunkGenerator() {

    private val entityStats = mapOf<String, Any>(
        ApplicationValues.Stats.NAME to "Name",
        ApplicationValues.Stats.HP to 2000
    )

    override fun onGenerate(chunk: Chunk, position: Vector2) {
        if (random.nextInt(0, 20) > 1) return
        val entityId = artemisWorld.create()
        entitySystem.createEntity(
            SystemEvent.CreateEntity(
                entityId = entityId,
                textureType = TextureType.ZOMBIE,
                entityType = EntityType.ENTITY,
                isObserver = false,
                isPhysical = true,
                entityStats = entityStats
            ))
        physicsSystem.createBody(
            SystemEvent.CreateBody(
                entityId = entityId,
                vector2 = position,
                bodyType = BodyType.CIRCLE,
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
    }

}