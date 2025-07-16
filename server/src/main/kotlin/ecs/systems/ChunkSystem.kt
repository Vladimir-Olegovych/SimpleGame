package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import org.example.ecs.components.EntityModel
import org.example.eventbus.ServerEventBus
import org.example.eventbus.event.BusEvent
import org.example.values.GameValues
import tools.artemis.systems.IteratingTaskSystem
import tools.chunk.Chunk
import tools.chunk.ChunkListener
import tools.chunk.ChunkManager
import tools.eventbus.annotation.EventCallback
import tools.math.IntVector2
import type.EntityType

@All(EntityModel::class)
class ChunkSystem: IteratingTaskSystem() {
    @Wire private lateinit var serverEventBus: ServerEventBus

    private lateinit var chunkManager: ChunkManager
    private lateinit var entityMapper: ComponentMapper<EntityModel>

    private val chunkListener = object : ChunkListener {
        override fun onChunkCreate(chunk: Chunk) {
            val entityId = world.create()

            serverEventBus.sendEvent(BusEvent.CreateEntity(
                entityId, false, EntityType.ENEMY
            ))

            serverEventBus.sendEvent(BusEvent.CreateBody(
                chunk.getWorldPosition(), entityId
            ))

            applyEntityChunk(entityId, chunk)
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

        for (x in -10 .. 10){
            for (y in -10 .. 10){
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