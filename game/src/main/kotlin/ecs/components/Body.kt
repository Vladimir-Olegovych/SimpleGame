package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import java.time.Instant

class Body: Component() {
    var renderPosition = Vector2.Zero

    val serverPositions = ArrayDeque<Pair<Vector2, Instant>>(MAX_DEQUE)

    fun hasInterpolationData() = serverPositions.size >= MAX_DEQUE - 1

    fun updateServerPosition(position: Vector2) {
        serverPositions.addLast(position to Instant.now())
        if (serverPositions.size > MAX_DEQUE - 1) serverPositions.removeFirst()
    }


    companion object {
        const val MAX_DEQUE = 3
    }
}