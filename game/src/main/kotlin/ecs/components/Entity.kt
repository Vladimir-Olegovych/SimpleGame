package ecs.components

import com.artemis.Component
import type.EntityType

class Entity: Component() {
    var entityType = EntityType.NULL
    var x = 0F
    var y = 0F
}