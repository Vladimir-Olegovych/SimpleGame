package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.example.constants.WorldComponents
import org.example.ecs.components.Entity
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.SendFeature
import tools.physics.createCircleEntity
import kotlin.random.Random

@All(Entity::class)
class PhysicSystem: IteratingSystem() {

    private lateinit var entityMapper: ComponentMapper<Entity>
    private val box2dWold = World(Vector2(0F, 0F), false)

    override fun initialize() {
        WorldComponents.setBox2dWorld(box2dWold)
        for (i in 0 until 100) {
            val entityId = world.create()
            val entity = entityMapper.create(entityId)
            entity.body = box2dWold.createCircleEntity(
                x = Random.nextInt(0, 100).toFloat(),
                y = Random.nextInt(0, 1000).toFloat(),
                restitution = 1F,
                radius = 1F,
                linearDamping = 0.1F,
                angularDamping = 0.1F
            )
        }
    }

    override fun process(entityId: Int) {
        box2dWold.step(world.delta, 8, 8)
        PlayerFeature.notify(entityId)
        ForceFeature.notify(entityId)
        SendFeature.notify(entityId)
    }

    override fun dispose() {
        WorldComponents.setBox2dWorld(null)
        box2dWold.dispose()
    }
}