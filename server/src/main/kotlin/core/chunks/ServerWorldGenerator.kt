package org.example.core.chunks

import alexey.tools.common.level.Chunk
import com.artemis.World
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import models.TextureType
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.BodyType
import org.example.core.models.ServerPreference
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import tools.chunk.ChunkGenerator
import type.EntityType
import kotlin.random.Random

class ServerWorldGenerator(
    private val artemisWorld: World,
    private val seed: Int = Random.nextInt(),
    serverPreferences: ServerPreference
): ChunkGenerator(
    chunkSize = serverPreferences.chunkSize,
    blockSize = serverPreferences.blockSize
){
    @Wire private lateinit var entitySystem: EntitySystem
    @Wire private lateinit var physicsSystem: PhysicsSystem
    @Wire private lateinit var chunkSystem: ChunkSystem

    override fun onCreateChunk(chunk: Chunk, positions: Array<Vector2>) {
        val chunkPosition = chunk.getPosition()
        val seed = (chunkPosition.x.toLong() shl 32) or (chunkPosition.y.toLong() and 0xFFFFFFFF)
        val random = Random(seed + this.seed)
        positions.forEach { position ->
            onCreateEntity(
                chunk = chunk,
                position = position,
                randInt = random.nextInt(0, 30)
            )
        }
    }

    private fun onCreateEntity(chunk: Chunk, position: Vector2, randInt: Int) {
        onGenerateFloor(position)
        when(randInt) {
            0 -> onGenerateEntity(position)
            1 -> onGenerateBlock(position)
        }
    }

    private fun onGenerateFloor(position: Vector2){
        val entityId = artemisWorld.create()
        entitySystem.createEntity(BusEvent.CreateEntity(
            entityId = entityId,
            textureType = TextureType.GRASS,
            entityType = EntityType.FLOOR,
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
            textureType = TextureType.ZOMBIE,
            entityType = EntityType.ENTITY,
            isObserver = false,
            isStatic = false,
            isPhysical = true
        ))
        physicsSystem.createBody(
            BusEvent.CreateBody(
                entityId = entityId,
                vector2 = position,
                bodyType = BodyType.CIRCLE,
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
    private fun onGenerateBlock(position: Vector2){
        val entityId = artemisWorld.create()
        val randTexture = when(Random.nextInt(0, 4)) {
            0 -> TextureType.LAVA
            1 -> TextureType.STONE
            2 -> TextureType.GRASS
            else -> TextureType.NULL
        }

        entitySystem.createEntity(BusEvent.CreateEntity(
            entityId = entityId,
            textureType = randTexture,
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