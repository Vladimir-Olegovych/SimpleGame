package org.example.core.level.chunks

import alexey.tools.common.level.Chunk
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.level.chunks.repository.ChunkGenerator
import org.example.ecs.event.SystemEvent
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import type.EntityType

class FloorChunkGenerator(
    private val artemisWorld: World,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem
): ChunkGenerator() {

    override fun onGenerate(chunk: Chunk, positions: Array<Vector2>) {
        for (position in positions) {
            val entityId = artemisWorld.create()
            entitySystem.createEntity(
                SystemEvent.CreateEntity(
                    entityId = entityId,
                    textureType = TextureType.GRASS,
                    entityType = EntityType.FLOOR,
                    isObserver = false,
                    isStatic = true,
                    isPhysical = false,
                    position = position
                )
            )
            chunkSystem.applyEntityChunk(
                SystemEvent.ApplyEntityToChunk(
                    entityId, position
                )
            )
        }
    }
}