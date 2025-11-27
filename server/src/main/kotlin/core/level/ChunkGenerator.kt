package org.example.core.level

import alexey.tools.common.level.Chunk
import kotlin.random.Random

open class ChunkGenerator {
    private var random: Random? = null
    private var chunk: Chunk? = null

    protected fun getRandom(): Random = random!!
    protected fun getChunk(): Chunk = chunk!!

    fun begin(chunk: Chunk, random: Random) {
        this.chunk = chunk
        this.random = random
    }

    fun end() {
        this.chunk = null
        this.random = null
    }

}