package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import models.TextureType
import type.EntityType

class EntityModel: Component() {
    var isStatic = false
    var updateTime = Long.MAX_VALUE
    var textureType = TextureType.NULL
    var entityType = EntityType.NULL
}