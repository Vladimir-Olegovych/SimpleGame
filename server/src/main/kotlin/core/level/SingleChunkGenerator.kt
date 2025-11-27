package org.example.core.level

import com.badlogic.gdx.math.Vector2

abstract class SingleChunkGenerator: ChunkGenerator() {
    abstract fun generatePosition(position: Vector2): Boolean
}