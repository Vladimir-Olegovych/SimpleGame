package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Enemy
import org.example.ecs.components.Entity
import org.example.ecs.components.Wall
import tools.artemis.features.Feature
import tools.physics.createCircleEntity
import tools.physics.createWall
import kotlin.random.Random

object WorldFeature: Feature() {

    @Wire private lateinit var box2dWold: World
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var enemyMapper: ComponentMapper<Enemy>
    private lateinit var wallMapper: ComponentMapper<Wall>

    override fun initialize() {
        for (i in 0 until 100) {
            val entityId = artemisWorld.create()
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
                val entityId = artemisWorld.create()
                val entity = entityMapper.create(entityId)
                val wall = wallMapper.create(entityId)
                wall.halfWidth = 0.5F
                wall.halfHeight = 0.5F
                entity.body = box2dWold.createWall(
                    x = i.toFloat() * 5F,
                    y = j.toFloat() * 5F,
                    halfWidth = wall.halfWidth,
                    halfHeight = wall.halfHeight,
                )
            }
        }
    }

    override fun process(entityId: Int) {

    }
}