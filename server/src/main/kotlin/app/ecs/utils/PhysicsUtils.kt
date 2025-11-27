package org.example.app.ecs.utils

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import org.example.app.ecs.components.*
import org.example.core.models.box2d.BodyType
import org.example.core.models.box2d.FixtureType
import org.example.core.models.settings.ServerPreference

fun World.utCreateBody(entityId: Int,
                      isEnabled: Boolean = true,
                      linearDamping: Float = 1F,
                      angularDamping: Float = 1F,
                      density: Float = 0.2F,
                      friction: Float = 1.3F,
                      restitution: Float = 1F,
                      bodyType: BodyType,
                      vector2: Vector2) {
    val world = this
    val box2dWorld = world.getRegistered(com.badlogic.gdx.physics.box2d.World::class.java)
    val serverPreference = world.getRegistered(ServerPreference::class.java)

    val textureComponentMapper = world.getMapper(TextureComponent::class.java)
    val entityTypeComponentMapper = world.getMapper(EntityTypeComponent::class.java)
    val moveComponentMapper = world.getMapper(MoveComponent::class.java)
    val itemComponentMapper = world.getMapper(ItemComponent::class.java)
    val staticPositionComponentMapper = world.getMapper(StaticPositionComponent::class.java)
    val entityComponentMapper = world.getMapper(EntityComponent::class.java)
    val statsComponentMapper = world.getMapper(StatsComponent::class.java)
    val physicsComponentMapper = world.getMapper(PhysicsComponent::class.java)
    val sizeComponentMapper = world.getMapper(SizeComponent::class.java)
    val inventoryComponentMapper = world.getMapper(InventoryComponent::class.java)

    val entity = entityComponentMapper[entityId]?: return
    val staticPosition = staticPositionComponentMapper[entityId]
    val physics = physicsComponentMapper.get(entityId)?: return
    val size = sizeComponentMapper.get(entityId)?: return

    fun getShape(): Shape {
        return when(bodyType){
            BodyType.CIRCLE -> CircleShape().also { it.radius = size.radius }
            BodyType.SQUARE -> PolygonShape().apply {
                setAsBox(size.halfWidth, size.halfHeight, Vector2.Zero, 0F)
            }
        }
    }

    val bDef =  BodyDef()
    bDef.type = if(staticPosition != null) BodyDef.BodyType.StaticBody else BodyDef.BodyType.DynamicBody
    bDef.linearDamping = linearDamping
    bDef.angularDamping = angularDamping
    bDef.position.set(vector2)

    val currentFDef = FixtureDef()
    currentFDef.shape = getShape()
    currentFDef.density = density
    currentFDef.friction = friction
    currentFDef.restitution = restitution

    val body = box2dWorld.createBody(bDef)
    body.createFixture(currentFDef).userData = FixtureType.Body(entityId)

    val sensorFDef = FixtureDef()
    sensorFDef.shape = CircleShape().also { it.radius = serverPreference.sensorRadius }
    sensorFDef.isSensor = true
    body.createFixture(sensorFDef).userData = FixtureType.Sensor(entityId)

    body.isActive = isEnabled
    physics.body = body
}

fun World.utRemoveBody(entityId: Int){
    val world = this.getRegistered(com.badlogic.gdx.physics.box2d.World::class.java)
    val physicsComponentMapper = this.getMapper(PhysicsComponent::class.java)
    physicsComponentMapper[entityId]?.let { component ->
        component.body?.let { body ->
            world.destroyBody(body)
        }
    }
}
