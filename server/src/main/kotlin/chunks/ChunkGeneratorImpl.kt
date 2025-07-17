package org.example.chunks

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import tools.chunk.Chunk
import tools.chunk.ChunkGenerator
import type.EntityType
import kotlin.random.Random

class ChunkGeneratorImpl(private val world: World,
                         private val serverEventBus: ServerEventBus,
                         chunkSize: Vector2,
                         blockSize: Float): ChunkGenerator(chunkSize, blockSize) {

    private val random = Random(12)

    override fun onCreateChunk(chunk: Chunk, positions: Array<Vector2>) {
        for (position in positions) {
            val result = random.nextInt(0, 2)
            if (result > 0) continue
            val entityId = world.create()

            serverEventBus.sendEvent(
                BusEvent.CreateEntity(
                    entityId, false, EntityType.ENEMY
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
}