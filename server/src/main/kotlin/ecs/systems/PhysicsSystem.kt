package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Size
import org.example.eventbus.event.BusEvent
import org.example.models.FixtureType
import org.example.values.GameValues
import tools.artemis.systems.BaseTaskSystem
import tools.eventbus.annotation.EventCallback
import tools.physics.createCircleEntity
import tools.physics.setSensorRadius

class PhysicsSystem: BaseTaskSystem() {

    @Wire private lateinit var box2dWold: World
    private lateinit var entityModelMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>

    @EventCallback
    private fun createBody(busEvent: BusEvent.CreateBody) {
        val entity = entityModelMapper.get(busEvent.entityId)?: return
        val size = sizeMapper.get(busEvent.entityId)

        addTask {
            val body = box2dWold.createCircleEntity(
                x = busEvent.vector2.x,
                y = busEvent.vector2.y,
                userData = FixtureType.Entity(busEvent.entityId),
                restitution = 1F,
                radius = size.radius,
                linearDamping = 1F,
                angularDamping = 1F
            )
            body.setSensorRadius(
                userData = FixtureType.Sensor(busEvent.entityId),
                radius = GameValues.getServerPreference().sensorRadius
            )
            entity.body = body
        }
    }

    @EventCallback
    private fun removeBody(busEvent: BusEvent.RemoveBody) {
        val entity = entityModelMapper.get(busEvent.entityId)?: return
        val body = entity.body?: return

        addTask {
            box2dWold.destroyBody(body)
        }
    }

    @EventCallback
    private fun pauseBody(busEvent: BusEvent.PauseBody) {
        val entity = entityModelMapper.get(busEvent.entityId) ?: return
        addTask {
            val body = entity.body
            body?.isActive = false
        }
    }

    @EventCallback
    private fun resumeBody(busEvent: BusEvent.ResumeBody) {
        val entity = entityModelMapper.get(busEvent.entityId) ?: return
        addTask {
            val body = entity.body
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