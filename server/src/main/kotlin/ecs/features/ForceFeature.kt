package org.example.ecs.features

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import org.example.ecs.components.Entity
import org.example.values.GameValues.MAX_SPEED
import tools.artemis.features.Feature

object ForceFeature: Feature() {

    private lateinit var entityMapper: ComponentMapper<Entity>

    fun applyForce(entityId: Int, x: Float, y: Float){
        val entity = entityMapper[entityId]?: return
        val force = Vector2(x, y)

        when {
            force.x > 0 -> force.x = MAX_SPEED
            force.x < 0 -> force.x = -MAX_SPEED
        }
        when {
            force.y > 0 -> force.y = MAX_SPEED
            force.y < 0 -> force.y = -MAX_SPEED
        }

        entity.moveForce.x = force.x
        entity.moveForce.y = force.y
    }

    override fun initialize() {}

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return

        val body = entity.body?: return
        body.applyForce(entity.moveForce, body.worldCenter, true)
    }
}