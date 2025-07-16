package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import type.EntityType

class EntityModel: Component() {
    var entityType = EntityType.NULL
    var position: Vector2? = null
    var updateTime: Long = -1L
}