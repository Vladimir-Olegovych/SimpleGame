package org.example.app.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.app.ecs.components.MoveComponent
import org.example.app.ecs.components.PhysicsComponent

@All(MoveComponent::class)
class MoveSystem: IteratingSystem() {

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var moveComponentMapper: ComponentMapper<MoveComponent>

    override fun process(entityId: Int) {
        val physicsComponent = physicsComponentMapper[entityId]?: return
        val moveComponent = moveComponentMapper[entityId]?: return
        val body = physicsComponent.body?: return
        val moveVector = moveComponent.vector
        body.applyForce(moveVector, body.worldCenter, true)
    }
}