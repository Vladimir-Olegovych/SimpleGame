package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import org.example.ecs.components.EntityModel
import org.example.ecs.processors.impl.ChunkProcessor
import org.example.eventbus.event.BusEvent
import org.example.values.GameValues
import tools.artemis.systems.IteratingTaskSystem
import tools.chunk.ChunkManager
import tools.chunk.ChunkManager.MutableChunk
import tools.eventbus.annotation.EventCallback
import tools.math.ImmutableIntVector2

@All(EntityModel::class)
class ChunkSystem: IteratingTaskSystem() {

    @Wire private lateinit var chunkProcessor: ChunkProcessor
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
        val preference = GameValues.getServerPreference()
        chunkManager = ChunkManager(
            processedRadius = preference.chunkRadius,
            chunkSize = preference.chunkSize.toVector2()
        ).apply { setChunkListener(chunkProcessor) }
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val body = entity.body?: return

        val currentChunk = chunkManager.getChunkByEntity(entityId)?: return
        val nextChunk = chunkManager.getChunkByWorld(body.position)?: return

        if (nextChunk.getPosition() == currentChunk.getPosition()) return
        chunkManager.move(entityId, nextChunk.getPosition())
    }
}