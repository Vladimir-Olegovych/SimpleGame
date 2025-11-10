package org.example.core.level.chunks.repository

import com.badlogic.gdx.math.Vector2

abstract class MultipleChunkGenerator: ChunkGenerator() {
    abstract fun busyAfterGenerate(): Boolean
    abstract fun generatePositions(positions: Array<Vector2>): Boolean
}