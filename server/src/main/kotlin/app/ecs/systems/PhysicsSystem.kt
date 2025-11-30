package org.example.app.ecs.systems

import app.ecs.components.ActiveComponent
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.app.ecs.components.PhysicsComponent

@All(PhysicsComponent::class, ActiveComponent::class)
class PhysicsSystem: BaseEntitySystem() {

    @Wire private lateinit var box2dWold: World

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>

    override fun inserted(entityId: Int) {
        val physicsComponent = physicsComponentMapper[entityId]?: return
        physicsComponent.body?.let { body ->
            body.isActive = true
        }
    }

    override fun removed(entityId: Int) {
        val physicsComponent = physicsComponentMapper[entityId]?: return
        physicsComponent.body?.let { body ->
            body.isActive = false
        }
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 3)
    }

}