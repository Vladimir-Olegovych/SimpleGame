package org.example.ecs.components

import com.artemis.Component
import type.EntityType

class EntityModel: Component() {
    var entityType = EntityType.NULL
    var isObserver = false
    var isStatic = false
    var isPhysical = false
}