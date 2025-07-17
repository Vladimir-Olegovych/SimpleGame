package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import org.example.chunks.ChunkGeneratorImpl
import org.example.ecs.components.EntityModel
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import org.example.values.GameValues
import tools.artemis.systems.IteratingTaskSystem
import tools.chunk.Chunk
import tools.chunk.ChunkGenerator
import tools.chunk.ChunkListener
import tools.chunk.ChunkManager
import tools.eventbus.annotation.EventCallback
import tools.math.IntVector2

@All(EntityModel::class)
class ChunkSystem: IteratingTaskSystem() {
    @Wire private lateinit var serverEventBus: ServerEventBus

    private lateinit var chunkGenerator: ChunkGenerator
    private lateinit var chunkManager: ChunkManager
    private lateinit var entityMapper: ComponentMapper<EntityModel>

    private val chunkListener = object : ChunkListener {
        override fun onChunkCreate(chunk: Chunk) {
            chunkGenerator.notify(chunk)
        }

        override fun onChunkEnabled(chunk: Chunk) {
            for (entityId in chunk.getEntities()){
                val entity = entityMapper[entityId]?: return
                addTask {
                    val body = entity.body
                    body?.isActive = true
                }
            }
        }

        override fun onChunkDisable(chunk: Chunk) {
            for (entityId in chunk.getEntities()){
                val entity = entityMapper[entityId]?: return
                addTask {
                    val body = entity.body
                    body?.isActive = false
                }
            }
        }

    }
    @EventCallback
    private fun removeEntityChunk(busEvent: BusEvent.RemoveEntityChunk){
        chunkManager.remove(busEvent.entityId)
    }

    @EventCallback
    private fun applyEntityChunk(busEvent: BusEvent.ApplyEntityToChunk){
        val chunk = chunkManager.obtain(busEvent.vector2)
        applyEntityChunk(busEvent.entityId, chunk)
    }

    private fun applyEntityChunk(entityId: Int, chunk: Chunk){
        val entity = entityMapper[entityId]
        chunk.addEntity(entityId)
        if (!entity.isObserver) return
        chunk.addObserver(entityId)
    }

    override fun initialize() {
        val preference = GameValues.getServerPreference()
        chunkManager = ChunkManager(
            processedRadius = preference.chunkRadius,
            chunkSize = preference.chunkSize.toVector2()
        ).apply { setChunkListener(chunkListener) }

        chunkGenerator = ChunkGeneratorImpl(world, serverEventBus, chunkManager.chunkSize, 0.2F)

        for (x in -1 .. 1){
            for (y in -1 .. 1){
                val chunk = chunkManager.createChunk(IntVector2(x, y))
            }
        }
    }

    override fun begin() {
        getAddTasks().forEach { it.invoke() }
        clearTasks()
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val body = entity.body?: return
        val bodyChunk = chunkManager.getChunkByWorld(body.position)?: return
        val entityChunk = chunkManager.getChunkByEntity(entityId)?: return
        if (bodyChunk.getPosition() == entityChunk.getPosition()) return
        chunkManager.move(entityId, bodyChunk.getPosition())
    }
}