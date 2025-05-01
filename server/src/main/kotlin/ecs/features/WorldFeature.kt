package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Enemy
import org.example.ecs.components.Entity
import org.example.ecs.components.Player
import org.example.ecs.components.Wall
import tools.artemis.features.Feature
import tools.physics.createCircleEntity
import tools.physics.createWall
import kotlin.random.Random

object WorldFeature: Feature() {

    @Wire private lateinit var box2dWold: World

    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var enemyMapper: ComponentMapper<Enemy>
    private lateinit var playerMapper: ComponentMapper<Player>
    private lateinit var wallMapper: ComponentMapper<Wall>

    fun createWall(x: Float = 0F,
                   y: Float = 0F,
                   halfWidth: Float = 1F,
                   halfHeight: Float = 1F
    )  {
        val entityId = artemisWorld.create()
        val entity = entityMapper.create(entityId)
        val wall = wallMapper.create(entityId)
        wall.halfWidth = halfWidth
        wall.halfHeight = halfHeight
        entity.body = box2dWold.createWall(
            x = x,
            y = y,
            halfWidth = halfWidth,
            halfHeight = halfHeight,
        )
    }

    fun createEnemy(x: Float = 0F,
                    y: Float = 0F,
                    restitution: Float = 1F,
                    radius: Float = 1F,
                    linearDamping: Float = 0.2F,
                    angularDamping: Float = 0.2F
    ) {
        val entityId = artemisWorld.create()
        val entity = entityMapper.create(entityId)
        val enemy = enemyMapper.create(entityId)
        enemy.radius = radius
        entity.body = box2dWold.createCircleEntity(
            x = x,
            y = y,
            restitution = restitution,
            radius = radius,
            linearDamping = linearDamping,
            angularDamping = angularDamping
        )
    }

    fun createPlayer(entityId: Int,
                     x: Float = 0F,
                     y: Float = 0F,
                     restitution: Float = 1F,
                     radius: Float = 1F,
                     linearDamping: Float = 0.1F,
                     angularDamping: Float = 0.1F
    ) = tasks.add {
        val entity = entityMapper.create(entityId)
        val player = playerMapper.create(entityId)
        player.radius = radius
        entity.body = box2dWold.createCircleEntity(
            x = x,
            y = y,
            restitution = restitution,
            radius = radius,
            linearDamping = linearDamping,
            angularDamping = angularDamping
        )
    }


    fun removePlayer(entityId: Int) = tasks.add {
        val entity = entityMapper[entityId]
        entity.body?.let { box2dWold.destroyBody(it) }
        entity.body = null

        entityMapper.remove(entityId)
        playerMapper.remove(entityId)
    }

    override fun initialize() {
        for (i in 0 until 100) {
            createEnemy(
                x = Random.nextInt(0, 100).toFloat(),
                y = Random.nextInt(0, 1000).toFloat()
            )
        }

        for (i in 0 until 10) {
            for (j in 0 until 10) {
                createWall(i * 5F, j * 5F)
            }
        }
    }

    override fun process(entityId: Int) {}
}