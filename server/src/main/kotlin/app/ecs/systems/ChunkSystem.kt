package org.example.app.ecs.systems

import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.components.PhysicsComponent
import org.example.app.level.chunks.BlockChunkGenerator
import org.example.app.level.chunks.EntityChunkGenerator
import org.example.app.level.chunks.FloorChunkGenerator
import org.example.app.level.generator.ServerWorldGenerator
import org.example.core.items.manager.ItemsManager
import org.example.core.level.ChunkGenerator
import org.example.core.models.settings.ServerPreference
import tools.chunk.WorldGenerator

@All(EntityComponent::class)
class ChunkSystem: ChunkManager.Listener, IteratingSystem() {

    @Wire private lateinit var chunkManager: AdvancedChunkManager
    @Wire private lateinit var serverPreference: ServerPreference
    @Wire private lateinit var itemsManager: ItemsManager

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var chunkGenerator: WorldGenerator

    override fun initialize() {
        chunkGenerator = ServerWorldGenerator(
            serverPreference = serverPreference,
            singleGenerators = arrayOf(
                EntityChunkGenerator(world, chunkManager, itemsManager),
                BlockChunkGenerator(world, chunkManager)
            ),
            multipleGenerators = arrayOf(
                FloorChunkGenerator(world, chunkManager)
            ),
        )
    }

    override fun onCreate(chunk: Chunk) {
        chunkGenerator.generateChunk(chunk)
    }

    override fun process(entityId: Int) {
        val physics = physicsComponentMapper[entityId]?: return
        val body = physics.body?: return

        if (!body.isActive || !body.isAwake) return
        val currentChunk = chunkManager.getChunk(entityId)?: return
        val nextChunk = chunkManager.obtainChunk(body.position)
        if (nextChunk.getPosition() == currentChunk.getPosition()) return
        chunkManager.moveTo(entityId, nextChunk.getPosition())
        chunkManager.updateOrigin(entityId)
    }
}