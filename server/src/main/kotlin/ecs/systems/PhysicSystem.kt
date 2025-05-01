package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.example.constants.WorldComponents
import org.example.ecs.components.Entity
import org.example.ecs.components.game.Enemy
import org.example.ecs.components.game.Wall
import org.example.ecs.features.ForceFeature
import org.example.ecs.features.PlayerFeature
import org.example.ecs.features.send.SendEnemyFeature
import org.example.ecs.features.send.SendPlayerFeature
import tools.physics.createCircleEntity
import tools.physics.createWall
import kotlin.random.Random

@All(Entity::class)
class PhysicSystem: IteratingSystem() {

    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var enemyMapper: ComponentMapper<Enemy>
    private lateinit var wallMapper: ComponentMapper<Wall>
    private val box2dWold = World(Vector2(0F, 0F), false)

    override fun initialize() {
        PlayerFeature.initialize(world)
        ForceFeature.initialize(world)
        SendEnemyFeature.initialize(world)
        SendPlayerFeature.initialize(world)
        WorldComponents.setBox2dWorld(box2dWold)

        for (i in 0 until 100) {
            val entityId = world.create()
            val entity = entityMapper.create(entityId)
            val enemy = enemyMapper.create(entityId)
            entity.body = box2dWold.createCircleEntity(
                x = Random.nextInt(0, 100).toFloat(),
                y = Random.nextInt(0, 1000).toFloat(),
                restitution = 1F,
                radius = 1F,
                linearDamping = 0.1F,
                angularDamping = 0.1F
            )
        }

        for (i in 0 until 10) {
            for (j in 0 until 10) {
                val entityId = world.create()
                val entity = entityMapper.create(entityId)
                val wall = wallMapper.create(entityId)
                entity.body = box2dWold.createWall(
                    x = i.toFloat() * 5F,
                    y = j.toFloat() * 5F,
                    halfWidth = 0.5F,
                    halfHeight = 0.5F,
                )
            }
        }
    }

    override fun process(entityId: Int) {
        box2dWold.step(world.delta, 8, 8)
        PlayerFeature.notify(entityId)
        ForceFeature.notify(entityId)
        SendEnemyFeature.notify(entityId)
        SendPlayerFeature.notify(entityId)
    }

    override fun dispose() {
        WorldComponents.setBox2dWorld(null)
        box2dWold.dispose()
    }
}