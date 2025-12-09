package app.ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class StaticPositionComponent: Component() {
    val position = Vector2(0f, 0f)
}