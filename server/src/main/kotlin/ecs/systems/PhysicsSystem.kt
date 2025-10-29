package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import org.example.core.models.BodyType
import org.example.core.models.FixtureType
import org.example.core.models.ServerPreference
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Physics
import org.example.ecs.components.Size
import org.example.ecs.event.SystemEvent
import tools.artemis.systems.BaseTaskSystem
import tools.physics.setSensorRadius

class PhysicsSystem: BaseTaskSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    private val box2dWold: World = World(Vector2(0F, 0F), false)

    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var physicsMapper: ComponentMapper<Physics>

    fun createBody(systemEvent: SystemEvent.CreateBody) {
        val entity = entityMapper[systemEvent.entityId]?: return
        val physics = physicsMapper.get(systemEvent.entityId)?: return
        val size = sizeMapper.get(systemEvent.entityId)?: return

        fun getShape(): Shape {
            return when(systemEvent.bodyType){
                BodyType.CIRCLE -> CircleShape().also { it.radius = size.radius }
                BodyType.SQUARE -> PolygonShape().apply {
                    setAsBox(size.halfWidth, size.halfHeight, Vector2.Zero, 0F)
                }
            }
        }

        addTask {
            val bDef =  BodyDef()
            bDef.type = if(entity.isStatic) BodyDef.BodyType.StaticBody else BodyDef.BodyType.DynamicBody
            bDef.linearDamping = systemEvent.linearDamping
            bDef.angularDamping = systemEvent.angularDamping
            bDef.position.set(systemEvent.vector2)

            val fixtureDef = FixtureDef()
            fixtureDef.shape = getShape()
            fixtureDef.density = systemEvent.density
            fixtureDef.friction = systemEvent.friction
            fixtureDef.restitution = systemEvent.restitution

            val body = box2dWold.createBody(bDef)
            body.createFixture(fixtureDef).userData = FixtureType.Entity(systemEvent.entityId)
            body.isActive = systemEvent.isEnabled
            body.setSensorRadius(
                userData = FixtureType.Sensor(systemEvent.entityId),
                radius = serverPreference.sensorRadius
            )

            physics.body = body
        }
    }

    fun removeBody(systemEvent: SystemEvent.RemoveBody) {
        val physics = physicsMapper.get(systemEvent.entityId)?: return
        val body = physics.body?: return

        addTask {
            box2dWold.destroyBody(body)
        }
    }

    fun pauseBody(systemEvent: SystemEvent.PauseBody) {
        val physics = physicsMapper.get(systemEvent.entityId)?: return
        addTask {
            val body = physics.body
            body?.isActive = false
        }
    }

    fun resumeBody(systemEvent: SystemEvent.ResumeBody) {
        val physics = physicsMapper.get(systemEvent.entityId)?: return
        addTask {
            val body = physics.body
            body?.isActive = true
        }
    }

    override fun begin() {
        getAddTasks().forEach { it.invoke() }
        clearTasks()
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 3)
    }
}