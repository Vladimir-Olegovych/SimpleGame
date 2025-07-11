package org.example.ecs.systems

import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.ecs.components.EntityModel
import org.example.values.GameValues
import tools.chunk.Chunk
import tools.chunk.ChunkListener
import tools.chunk.ChunkManager

@All(EntityModel::class)
class ChunkSystem: IteratingSystem() {

    private lateinit var chunkManager: ChunkManager

    private val chunkListener = object : ChunkListener {
        override fun onChunkCreate(chunk: Chunk) {

        }

        override fun onChunkEnabled(chunk: Chunk) {

        }

        override fun onChunkDisable(chunk: Chunk) {

        }

    }

    override fun initialize() {
        val preference = GameValues.getServerPreference()
        chunkManager = ChunkManager(
            processedRadius = preference.chunkRadius,
            chunkSize = preference.chunkSize.toVector2()
        ).apply { setChunkListener(chunkListener) }
    }

    override fun begin() {

    }

    override fun process(entityId: Int) {

    }
}