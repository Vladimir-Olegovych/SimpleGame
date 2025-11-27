package org.example.app.level.generator

import alexey.tools.common.level.Chunk
import com.badlogic.gdx.math.Vector2
import org.example.core.level.MultipleChunkGenerator
import org.example.core.level.SingleChunkGenerator
import org.example.core.models.settings.ServerPreference
import tools.chunk.WorldGenerator
import kotlin.random.Random

class ServerWorldGenerator(serverPreference: ServerPreference,
                           private val singleGenerators: Array<SingleChunkGenerator>,
                           private val multipleGenerators: Array<MultipleChunkGenerator>
): WorldGenerator(
    chunkSize = serverPreference.chunkSize,
    blockSize = serverPreference.blockSize
){
    //private val seed: Int = -1449399422
    private val seed: Int = Random.nextInt()

    init {
        println("World seed: $seed")
    }

    override fun onGenerateChunk(
        chunk: Chunk,
        positions: Array<Vector2>
    ) {
        val chunkPosition = chunk.getPosition()
        val seed = (chunkPosition.x.toLong() shl 32) or (chunkPosition.y.toLong() and 0xFFFFFFFF)
        val random = Random(seed + this.seed)

        var busyChunk = false
        for (generator in multipleGenerators) {
            if (generator.busyAfterGenerate() && busyChunk) continue
            generator.begin(chunk, random)
            val isBusy = generator.generatePositions(positions)
            if (isBusy) busyChunk = true
            generator.end()
        }

        if (busyChunk) return
        val busyPositions = HashMap<Vector2, Boolean>()
        for (generator in singleGenerators) {
            generator.begin(chunk, random)
            for (position in positions) {
                if (busyPositions[position] == true) continue
                val isBusy = generator.generatePosition(position)
                busyPositions[position] = isBusy
            }
            generator.end()
        }
    }
}