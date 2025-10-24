package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import ecs.components.Client
import event.Event
import models.SendType
import org.example.core.eventbus.event.BusEvent
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Physics
import org.example.ecs.components.Size
import org.example.ecs.components.StaticPosition
import tools.artemis.systems.IteratingTaskSystem

@All(Client::class)
class EventSystem: IteratingTaskSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var staticPositionMapper: ComponentMapper<StaticPosition>
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var sizeMapper: ComponentMapper<Size>

    override fun begin() {
        getAddTasks().forEach { it.invoke() }
        clearTasks()
    }

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return
        for (entityId in client.getEntities()) {
            client.processEntityBody(entityId)
        }
    }

    private fun Client.processEntityBody(id: Int){
        val physics = physicsMapper[id]?: return
        val entity = entityMapper[id]?: return
        val entityBody = physics.body?: return

        if (entity.isStatic) return
        if (!entityBody.isActive) return
        if (!entityBody.isAwake) return

        addEvent(
            Event.Position(
                entityId = id,
                x = entityBody.position.x,
                y = entityBody.position.y
            ),
            sendType = SendType.UDP
        )

        addEvent(
            Event.Angle(
                entityId = id,
                angle = physics.body?.angle?: 0F
            ),
            sendType = SendType.UDP
        )
    }

    private fun Client.processEntity(id: Int){
        val entity = entityMapper[id]?: return
        addEvent(
            Event.Entity(
                entityId = id,
                isStatic = entity.isStatic,
                textureType = entity.textureType,
                entityType = entity.entityType
            )
        )

        sizeMapper[id]?.let {
            addEvent(
                Event.Size(
                    entityId = id,
                    radius = it.radius,
                    halfHeight = it.halfHeight,
                    halfWidth = it.halfWidth
                )
            )
        }
        staticPositionMapper[id]?.position?.let {
            addEvent(
                Event.Position(
                    entityId = id,
                    x = it.x,
                    y = it.y,
                )
            )
        }
    }

    fun showEntities(busEvent: BusEvent.ShowEntities){
        val client = clientMapper[busEvent.entityId]?: return
        client.addEntities(busEvent.entities)
        for (entityId in busEvent.entities) {
            addTask { client.processEntity(entityId) }
        }
    }

    fun hideEntities(busEvent: BusEvent.HideEntities) {
        val client = clientMapper[busEvent.entityId] ?: return
        client.removeEntities(busEvent.entities)
    }
}