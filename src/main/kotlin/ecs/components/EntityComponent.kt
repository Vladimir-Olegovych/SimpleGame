package ecs.components

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class EntityComponent: Component() {

    private var position: Vector2? = null

    fun initialize(position: Vector2) {
        if (this.position != null) error("EntityComponent already initialized!")
        this.position = position
    }

    fun hasPosition(): Boolean {
        return position != null
    }

    fun getPosition(): Vector2 {
        return position!!
    }
}