package org.example.ecs.processors.impl

import com.artemis.World
import org.example.ecs.processors.GameProcessor
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import tools.chunk.Chunk
import tools.chunk.ChunkListener
import type.EntityType

class ChunkProcessor(
    private val serverEventBus: ServerEventBus,
): GameProcessor, ChunkListener {

    var artemisWorld: World? = null

    override fun onChunkCreate(chunk: Chunk) {
        val entityId = artemisWorld?.create()?: return

        serverEventBus.sendEvent(
            BusEvent.CreateEntity(
                entityId, EntityType.ENEMY, false
            )
        )

        serverEventBus.sendEvent(
            BusEvent.CreateBody(
                entityId, chunk.getWorldPosition()
            )
        )

        serverEventBus.sendEvent(
            BusEvent.ApplyEntityToChunk(
                entityId, chunk.getWorldPosition()
            )
        )
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

    override fun onMoved(entityId: Int, from: Chunk, to: Chunk) {
        if (to.getStatus()) return
        serverEventBus.sendEvent(BusEvent.PauseBody(entityId))
    }
}