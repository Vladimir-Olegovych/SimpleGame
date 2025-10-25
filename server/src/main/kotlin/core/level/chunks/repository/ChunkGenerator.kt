package org.example.core.level.chunks.repository

import alexey.tools.common.level.Chunk
import com.badlogic.gdx.math.Vector2
import kotlin.random.Random

open class ChunkGenerator {
    lateinit var random: Random

    open fun onGenerate(chunk: Chunk, positions: Array<Vector2>) {}
    open fun onGenerate(chunk: Chunk, position: Vector2) {}
}