package org.example.core.chunks

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import org.example.core.eventbus.ServerEventBus
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import tools.chunk.Chunk
import tools.chunk.ChunkGenerator
import type.EntityType
import kotlin.random.Random

class ServerChunkGenerator(
    private val artemisWorld: World,
    private val serverEventBus: ServerEventBus,
    serverPreferences: ServerPreference
): ChunkGenerator(
    chunkSize = serverPreferences.chunkSize.toVector2(),
    blockSize = serverPreferences.blockSize
){
    private val random = Random(12)

    override fun onCreateChunk(chunk: Chunk) {

    }

    override fun onCreateEntity(chunk: Chunk, position: Vector2) {
        val result = random.nextInt(0, 30)
        if (result >= 2) return
        val entityId = artemisWorld.create()

        serverEventBus.sendEvent(
            BusEvent.CreateEntity(
                entityId, EntityType.ENEMY, false
            )
        )

        serverEventBus.sendEvent(
            BusEvent.CreateBody(
                entityId, position
            )
        )

        serverEventBus.sendEvent(
            BusEvent.ApplyEntityToChunk(
                entityId, position
            )
        )
    }
}