package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.Entity
import org.example.ecs.components.Size
import org.example.models.FixtureType
import org.example.values.GameValues
import tools.artemis.features.Feature
import tools.physics.createCircleEntity
import tools.physics.createWall
import tools.physics.setSensorRadius
import type.EntityType


object WorldFeature: Feature() {

    @Wire private lateinit var box2dWold: World

    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var sizeMapper: ComponentMapper<Size>

    fun createWall(x: Float = 0F,
                   y: Float = 0F,
                   halfWidth: Float = 1F,
                   halfHeight: Float = 1F
    ) {
        val entityId = artemisWorld.create()
        val entity = entityMapper.create(entityId)
        val sizes = sizeMapper.create(entityId)
        sizes.halfWidth = halfWidth
        sizes.halfHeight = halfHeight
        entity.entityType = EntityType.WALL
        post {
            val body = box2dWold.createWall(
                x = x,
                y = y,
                userData = FixtureType.Entity(entityId),
                halfWidth = halfWidth,
                halfHeight = halfHeight,
            )
            entity.body = body
        }
    }

    fun createEnemy(x: Float = 0F,
                    y: Float = 0F,
                    restitution: Float = 1F,
                    radius: Float = 0.1F,
                    linearDamping: Float = 0.2F,
                    angularDamping: Float = 0.2F
    ) {
        val entityId = artemisWorld.create()
        val entity = entityMapper.create(entityId)
        entity.entityType = EntityType.ENEMY
        post {
            val body = box2dWold.createCircleEntity(
                x = x,
                y = y,
                userData = FixtureType.Entity(entityId),
                restitution = restitution,
                radius = radius,
                linearDamping = linearDamping,
                angularDamping = angularDamping
            )
            entity.body = body
        }
    }

    fun createPlayer(entityId: Int,
                     x: Float = 0F,
                     y: Float = 0F,
                     restitution: Float = 1F,
                     radius: Float = 0.3F,
                     linearDamping: Float = 0.1F,
                     angularDamping: Float = 0.1F
    ) {
        val entity = entityMapper.create(entityId)
        entity.entityType = EntityType.PLAYER
        entity.isObserver = true
        post {
            val body = box2dWold.createCircleEntity(
                x = x,
                y = y,
                userData = FixtureType.Entity(entityId),
                restitution = restitution,
                radius = radius,
                linearDamping = linearDamping,
                angularDamping = angularDamping
            )
            body.setSensorRadius(
                userData = FixtureType.Sensor(entityId),
                radius = GameValues.SENSOR_RADIUS
            )
            entity.body = body
        }
    }

    fun removeEntity(entityId: Int) {
        val entity = entityMapper[entityId]

        entity.body?.let { post { box2dWold.destroyBody(it) } }
        entity.body = null

        entityMapper.remove(entityId)
    }

    override fun initialize() {
        /*
        createEnemy(
            x = 2F,
            y = 2F
        )

         */
        for (i in 0 until 20) {
            for (j in 0 until 20) {
                createEnemy(
                    x = i * 0.2F,
                    y = j * 0.2F,
                )
            }
        }

    }

    override fun process(entityId: Int) {
    }
}