package org.example.ecs.features

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.IntMap
import org.example.ecs.components.Entity
import org.example.values.GameValues
import tools.artemis.features.Feature

object ForceFeature: Feature() {

    private val entityForces = IntMap<Vector2>()
    private lateinit var entityMapper: ComponentMapper<Entity>


    fun applyForce(entityId: Int, x: Float, y: Float){
        val force = Vector2(x, y)
        when {
            force.x > 0 -> force.x = GameValues.MAX_SPEED
            force.x < 0 -> force.x = -GameValues.MAX_SPEED
        }
        when {
            force.y > 0 -> force.y = GameValues.MAX_SPEED
            force.y < 0 -> force.y = -GameValues.MAX_SPEED
        }
        entityForces.put(entityId, force)
    }

    override fun initialize() {}

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val entityForce = entityForces[entityId]?: run {
            val force = Vector2.Zero
            entityForces.put(entityId, force); force
        }

        val body = entity.body?: return
        body.applyForce(entityForce, body.worldCenter, true)
    }
}