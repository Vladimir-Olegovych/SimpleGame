package org.example.ecs.processors

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import org.example.core.eventbus.event.BusEvent
import org.example.core.level.world.ServerWorldGenerator
import org.example.ecs.systems.EventSystem
import org.example.ecs.systems.PhysicsSystem
import tools.artemis.processor.GameProcessor

class ChunkProcessor(
    private val physicsSystem: PhysicsSystem,
    private val eventSystem: EventSystem,
    private val chunkGenerator: ServerWorldGenerator
): ChunkManager.Listener, GameProcessor {

    override fun onCreate(chunk: Chunk) {
        chunkGenerator.generateChunk(chunk)
    }

    override fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        if(first) for (entityId in entities){
            physicsSystem.resumeBody(BusEvent.ResumeBody(entityId))
        }
    }

    override fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        if(last) for (entityId in entities){
            physicsSystem.pauseBody(BusEvent.PauseBody(entityId))
        }
    }

    override fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        activators.forEach {
            eventSystem.showEntities(BusEvent.ShowEntities(it, entities))
        }
    }

    override fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        activators.forEach {
            eventSystem.hideEntities(BusEvent.HideEntities(it, entities))
        }
    }
}