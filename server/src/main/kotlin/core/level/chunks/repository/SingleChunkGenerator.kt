package org.example.core.level.chunks.repository

import com.badlogic.gdx.math.Vector2

abstract class SingleChunkGenerator: ChunkGenerator() {
    abstract fun generatePosition(position: Vector2): Boolean
}