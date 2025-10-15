package org.example.ecs.processors

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import com.artemis.World
import com.artemis.annotations.Wire
import org.example.core.chunks.ServerWorldGenerator
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import org.example.ecs.systems.EventSystem
import org.example.ecs.systems.PhysicsSystem
import tools.artemis.processor.GameProcessor

class ChunkProcessor(
    private val serverPreferences: ServerPreference
): ChunkManager.Listener, GameProcessor {

    @Wire private lateinit var physicsSystem: PhysicsSystem
    @Wire private lateinit var eventSystem: EventSystem
    private lateinit var chunkGenerator: ServerWorldGenerator

    override fun create(artemisWorld: World) {
        chunkGenerator = ServerWorldGenerator(
            artemisWorld = artemisWorld,
            serverPreferences = serverPreferences
        )
        artemisWorld.inject(chunkGenerator)
    }

    override fun onCreate(chunk: Chunk) {
        chunkGenerator.applyChunkCreation(chunk)
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