package org.example.core.level.world

import alexey.tools.common.level.Chunk
import com.badlogic.gdx.math.Vector2
import org.example.core.level.chunks.repository.ChunkGenerator
import org.example.core.models.ServerPreference
import tools.chunk.WorldGenerator
import kotlin.random.Random

class ServerWorldGenerator(serverPreference: ServerPreference,
                           private val generators: Array<ChunkGenerator>): WorldGenerator(
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

        for (generator in generators) {
            generator.random = random
            generator.onGenerate(chunk, positions)
        }
        for (position in positions) {
            for (generator in generators) {
               if(random.nextInt(0 ,10) < 1) generator.onGenerate(chunk, position)
            }
        }
    }
}