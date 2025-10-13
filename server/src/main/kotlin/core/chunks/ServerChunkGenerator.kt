package org.example.core.chunks

import alexey.tools.common.level.Chunk
import com.artemis.World
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import tools.chunk.ChunkGenerator
import type.EntityType
import kotlin.random.Random

class ServerChunkGenerator(
    private val artemisWorld: World,
    serverPreferences: ServerPreference
): ChunkGenerator(
    chunkSize = serverPreferences.chunkSize,
    blockSize = serverPreferences.blockSize
){
    @Wire private lateinit var entitySystem: EntitySystem
    @Wire private lateinit var physicsSystem: PhysicsSystem
    @Wire private lateinit var chunkSystem: ChunkSystem

    private val random = Random(12)

    override fun onCreateChunk(chunk: Chunk) {}

    override fun onCreateEntity(chunk: Chunk, position: Vector2) {
        val result = random.nextInt(0, 30)
        onGenerateFloor(position)
        if (result >= 4) return
        onGenerateEntity(position)
    }

    private fun onGenerateFloor(position: Vector2){
        val entityId = artemisWorld.create()
        entitySystem.createEntity(BusEvent.CreateEntity(
            entityId = entityId,
            entityType = EntityType.GRASS,
            isObserver = false,
            isStatic = true,
            isPhysical = false,
            position = position
        ))
        chunkSystem.applyEntityChunk(
            BusEvent.ApplyEntityToChunk(
                entityId, position
            )
        )
    }

    private fun onGenerateEntity(position: Vector2){
        val entityId = artemisWorld.create()
        entitySystem.createEntity(BusEvent.CreateEntity(
            entityId = entityId,
            entityType = EntityType.ENEMY,
            isObserver = false,
            isStatic = false,
            isPhysical = true
        ))
        physicsSystem.createBody(
            BusEvent.CreateBody(
                entityId, position
            )
        )
        chunkSystem.applyEntityChunk(
            BusEvent.ApplyEntityToChunk(
                entityId, position
            )
        )
    }
}