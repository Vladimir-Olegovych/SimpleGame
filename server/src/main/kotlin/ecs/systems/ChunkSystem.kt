package org.example.ecs.systems

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.core.eventbus.event.BusEvent
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Physics

@All(EntityModel::class)
class ChunkSystem: IteratingSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var entityMapper: ComponentMapper<EntityModel>

    fun removeEntityChunk(busEvent: BusEvent.RemoveEntityChunk){
        chunkManager.remove(busEvent.entityId)
    }

    fun applyEntityChunk(busEvent: BusEvent.ApplyEntityToChunk){
        val chunk = chunkManager.obtainChunk(busEvent.vector2)
        val entity = entityMapper[busEvent.entityId]
        chunk.add(busEvent.entityId, entity.isObserver)
    }

    override fun process(entityId: Int) {
        val physics = physicsMapper[entityId]?: return
        val body = physics.body?: return

        if (!body.isActive) return
        val currentChunk = chunkManager.getChunk(entityId)?: return
        val nextChunk = chunkManager.obtainChunk(body.position)
        if (nextChunk.getPosition() == currentChunk.getPosition()) return
        chunkManager.moveTo(entityId, nextChunk.getPosition())
        chunkManager.updateOrigin(entityId)
    }
}