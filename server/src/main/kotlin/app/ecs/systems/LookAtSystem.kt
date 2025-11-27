package org.example.app.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.app.ecs.components.LookAtComponent
import org.example.app.ecs.components.PhysicsComponent

@All(PhysicsComponent::class)
class LookAtSystem: IteratingSystem() {

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var lookAtComponentMapper: ComponentMapper<LookAtComponent>

    override fun process(entityId: Int) {
        val physicsComponent = physicsComponentMapper[entityId]?: return
        val lookAtComponent = lookAtComponentMapper[entityId]?: return
        val angle = lookAtComponent.lookAt?: return
        val body = physicsComponent.body?: return
        if (body.angle == angle) return
        body.setTransform(body.position, angle)
    }

}