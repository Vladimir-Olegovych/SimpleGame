package org.example.ecs.systems

import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Entity
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.WorldFeature
import org.example.ecs.features.send.SendEnemyFeature
import org.example.ecs.features.send.SendPlayerFeature
import org.example.ecs.features.send.SendWallFeature

@All(Entity::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var box2dWold: World

    override fun initialize() {
        WorldFeature.initialize(world)
        PlayerFeature.initialize(world)
        ForceFeature.initialize(world)
        SendEnemyFeature.initialize(world)
        SendPlayerFeature.initialize(world)
        SendWallFeature.initialize(world)
    }

    override fun process(entityId: Int) {
        box2dWold.step(world.delta, 8, 8)
        WorldFeature.notify(entityId)
        PlayerFeature.notify(entityId)
        ForceFeature.notify(entityId)
        SendEnemyFeature.notify(entityId)
        SendPlayerFeature.notify(entityId)
        SendWallFeature.notify(entityId)
    }

    override fun dispose() {
        box2dWold.dispose()
    }
}