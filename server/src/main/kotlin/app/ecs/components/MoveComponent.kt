package org.example.app.ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import kotlin.random.Random

class MoveComponent: Component() {
    val vector = Vector2(
        -1F + Random.nextFloat() * (1F - -1F), -1F + Random.nextFloat() * (1F - -1F)
    )
}