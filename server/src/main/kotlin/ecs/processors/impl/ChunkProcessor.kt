package org.example.ecs.processors.impl

import com.artemis.World
import org.example.core.chunks.ServerChunkGenerator
import org.example.ecs.processors.GameProcessor
import org.example.core.eventbus.ServerEventBus
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import tools.chunk.Chunk
import tools.chunk.ChunkListener

class ChunkProcessor(
    private val serverEventBus: ServerEventBus,
    private val serverPreferences: ServerPreference
): ChunkListener, GameProcessor {

    private lateinit var chunkGenerator: ServerChunkGenerator

    override fun create(artemisWorld: World) {
        chunkGenerator = ServerChunkGenerator(
            artemisWorld = artemisWorld,
            serverEventBus = serverEventBus,
            serverPreferences = serverPreferences
        )
    }

    override fun onChunkCreate(chunk: Chunk) {
        chunkGenerator.applyChunkCreation(chunk)
    }

    override fun onChunkEnabled(chunk: Chunk) {
        for (entityId in chunk.getEntities()){
            serverEventBus.sendEvent(BusEvent.ResumeBody(entityId))
        }
    }

    override fun onChunkDisable(chunk: Chunk) {
        for (entityId in chunk.getEntities()){
            serverEventBus.sendEvent(BusEvent.PauseBody(entityId))
        }
    }

    override fun onEntityObservable(entityId: Int, chunk: Chunk) {
        serverEventBus.sendEvent(BusEvent.EntityMovedOnChunk(entityId, chunk))
    }

    override fun onChunkLoaded(entityId: Int, chunks: List<Chunk>) {
        serverEventBus.sendEvent(BusEvent.LoadChunks(entityId, chunks))
    }

    override fun onChunkUnloaded(entityId: Int, chunks: List<Chunk>) {
        serverEventBus.sendEvent(BusEvent.UnloadChunks(entityId, chunks))
    }

    override fun onMoved(entityId: Int, from: Chunk, to: Chunk) {
        if (to.getStatus()) return
        serverEventBus.sendEvent(BusEvent.PauseBody(entityId))
    }
}