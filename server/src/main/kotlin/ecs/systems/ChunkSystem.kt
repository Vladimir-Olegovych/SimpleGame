package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.ecs.components.EntityModel
import org.example.ecs.processors.impl.ChunkProcessor
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.core.printChunksGrid
import tools.artemis.systems.IteratingTaskSystem
import tools.chunk.ChunkManager
import tools.eventbus.annotation.EventCallback
import tools.math.IntVector2

@All(EntityModel::class)
class ChunkSystem: IteratingSystem() {

    @Wire private lateinit var chunkProcessor: ChunkProcessor
    @Wire private lateinit var serverPreference: ServerPreference
    private lateinit var chunkManager: ChunkManager
    private lateinit var entityMapper: ComponentMapper<EntityModel>

    @EventCallback
    private fun removeEntityChunk(busEvent: BusEvent.RemoveEntityChunk){
        chunkManager.remove(busEvent.entityId)
    }

    @EventCallback
    private fun applyEntityChunk(busEvent: BusEvent.ApplyEntityToChunk){
        val chunk = chunkManager.obtain(busEvent.vector2)
        val entity = entityMapper[busEvent.entityId]

        chunk.addEntity(busEvent.entityId, entity.isObserver)
    }

    override fun initialize() {
        chunkProcessor.create(world)
        chunkManager = ChunkManager(
            processedRadius = serverPreference.chunkRadius,
            chunkSize = serverPreference.chunkSize.toVector2()
        ).apply {
            setChunkListener(chunkProcessor)
        }

    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val body = entity.body?: return

        if (!body.isActive) return
        val currentChunk = chunkManager.getChunkByEntity(entityId)?: return
        val nextChunk = chunkManager.getChunkByWorld(body.position)?: return

        if (nextChunk.getPosition() == currentChunk.getPosition()) return
        chunkManager.move(entityId, nextChunk.getPosition())
        //printChunksGrid(chunkManager.chunks)
    }
}