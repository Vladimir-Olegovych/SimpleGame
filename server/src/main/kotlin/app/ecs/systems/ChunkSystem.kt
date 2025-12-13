package org.example.app.ecs.systems

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import alexey.tools.server.level.AdvancedChunkManager
import app.ecs.components.ActiveComponent
import app.ecs.components.VisibleComponent
import app.event.ChunkEvent
import app.level.generator.ServerWorldGenerator
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import org.example.app.ecs.components.PhysicsComponent
import org.example.core.models.settings.ServerPreference
import tools.chunk.WorldGenerator
import tools.eventbus.EventBus

@All(PhysicsComponent::class)
class ChunkSystem: ChunkManager.Listener, IteratingSystem() {

    @Wire private lateinit var eventBus: EventBus
    @Wire private lateinit var chunkManager: AdvancedChunkManager
    @Wire private lateinit var serverPreference: ServerPreference

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var activeComponentMapper: ComponentMapper<ActiveComponent>
    private lateinit var visibleComponentMapper: ComponentMapper<VisibleComponent>
    private lateinit var chunkGenerator: WorldGenerator

    override fun initialize() {
        chunkGenerator = ServerWorldGenerator(
            artemisWorld = world,
            serverPreference = serverPreference
        )
    }

    override fun onCreate(chunk: Chunk) {
        chunkGenerator.generateChunk(chunk)
    }

    override fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        if(!first) return
        for (entityId in entities) {
            activeComponentMapper.create(entityId)
        }
    }

    override fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        if(!last) return
        for (entityId in entities) {
            activeComponentMapper.remove(entityId)
        }
    }

    override fun onShow(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        eventBus.sendEventNow(ChunkEvent.Show(
            entities = entities,
            activators = activators
        ))
        if(!first) return
        for (entityId in entities) {
            visibleComponentMapper.create(entityId)
        }
    }

    override fun onHide(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        eventBus.sendEventNow(ChunkEvent.Hide(
            entities = entities,
            activators = activators
        ))
        if(!last) return
        for (entityId in entities) {
            visibleComponentMapper.remove(entityId)
        }
    }

    override fun process(entityId: Int) {
        val physics = physicsComponentMapper[entityId]?: return
        val body = physics.getBody()?: return

        if (!body.isActive || !body.isAwake) return
        val currentChunk = chunkManager.getChunk(entityId)?: return
        val nextChunk = chunkManager.obtainChunk(body.position)
        if (nextChunk.getPosition() == currentChunk.getPosition()) return
        chunkManager.moveTo(entityId, nextChunk.getPosition())
        chunkManager.updateOrigin(entityId)
    }
}