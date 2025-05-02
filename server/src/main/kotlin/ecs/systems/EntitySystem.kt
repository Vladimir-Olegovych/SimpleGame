package org.example.ecs.systems

import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Entity
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.WorldFeature

@All(Entity::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var box2dWold: World

    override fun initialize() {
        WorldFeature.initialize(world)
        PlayerFeature.initialize(world)
        ForceFeature.initialize(world)
    }

    override fun process(entityId: Int) {
        box2dWold.step(world.delta, 8, 3)
        WorldFeature.notify(entityId)
        PlayerFeature.notify(entityId)
        ForceFeature.notify(entityId)
    }

    override fun dispose() {
        box2dWold.dispose()
    }
}