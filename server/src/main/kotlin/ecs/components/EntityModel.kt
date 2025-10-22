package org.example.ecs.components

import com.artemis.Component
import models.TextureType
import type.EntityType

class EntityModel: Component() {
    var entityType = EntityType.NULL
    var textureType = TextureType.NULL
    var isObserver = false
    var isStatic = false
    var isPhysical = false
}