package org.example.app.ecs.components

import com.artemis.Component
import models.enums.EntityType

class EntityTypeComponent: Component() {
    var entityType = EntityType.NULL
}