package org.example.app.level.chunks

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import org.example.core.level.MultipleChunkGenerator

class StructureChunkGenerator(
    private val artemisWorld: World
): MultipleChunkGenerator() {

    init { artemisWorld.inject(this) }

    override fun busyAfterGenerate(): Boolean {
        return true
    }

    override fun generatePositions(positions: Array<Vector2>): Boolean {
        return false
    }
}