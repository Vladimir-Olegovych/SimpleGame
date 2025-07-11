package org.example.ecs.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body
import type.EntityType

class EntityModel: Component() {
    var entityType = EntityType.NULL
    var body: Body? = null
    var isObserver = false
}