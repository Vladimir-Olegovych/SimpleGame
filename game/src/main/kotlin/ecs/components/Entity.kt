package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import type.EntityType

class Entity: Component() {
    var entityType = EntityType.NULL
    val position: Vector2 = Vector2(0F, 0F)
}