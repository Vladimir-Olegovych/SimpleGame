package org.example.ecs.systems

import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Entity
import org.example.ecs.features.*

@All(Entity::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var box2dWold: World

    override fun initialize() {
        WorldFeature.initialize(world)
        ContactFeature.initialize(world)
        PlayerFeature.initialize(world)
        ForceFeature.initialize(world)
    }

    override fun begin() {
        box2dWold.step(world.delta, 8, 3)
    }

    override fun process(entityId: Int) {
        WorldFeature.notify(entityId)
        ContactFeature.notify(entityId)
        PlayerFeature.notify(entityId)
        ForceFeature.notify(entityId)
    }

    override fun dispose() {
        box2dWold.dispose()
    }
}