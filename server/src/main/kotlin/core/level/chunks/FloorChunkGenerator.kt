package org.example.core.level.chunks

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.level.chunks.repository.MultipleChunkGenerator
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
): MultipleChunkGenerator() {

    override fun busyAfterGenerate(): Boolean {
        return false
    }

    override fun generatePositions(positions: Array<Vector2>): Boolean {
        positions.forEach { position ->
            val entityId = artemisWorld.create()
            entitySystem.createEntity(
                SystemEvent.CreateEntity(
                    entityId = entityId,
                    textureType = TextureType.GRASS,
                    entityType = EntityType.FLOOR,
                    isObserver = false,
                    isPhysical = false,
                    staticPosition = position
                )
            )
            chunkSystem.applyEntityChunk(
                SystemEvent.ApplyEntityToChunk(
                    entityId, position
                )
            )
        }
        return false
    }


}