package org.example.core.level.chunks

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.level.chunks.repository.MultipleChunkGenerator
import org.example.core.models.BodyType
import org.example.core.models.ServerPreference
import org.example.ecs.event.SystemEvent
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import type.EntityType

class StructureChunkGenerator(
    private val artemisWorld: World,
    private val serverPreference: ServerPreference,
    private val chunkManager: AdvancedChunkManager,
    private val entitySystem: EntitySystem,
    private val physicsSystem: PhysicsSystem,
    private val chunkSystem: ChunkSystem
): MultipleChunkGenerator() {

    override fun busyAfterGenerate(): Boolean {
        return true
    }

    override fun generatePositions(positions: Array<Vector2>): Boolean {
        return false
    }

    private fun generateBlock(position: Vector2){
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
    }
}