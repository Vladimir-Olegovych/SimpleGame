package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.core.eventbus.event.BusEvent
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Physics
import org.example.ecs.components.Size
import org.example.ecs.components.StaticPosition
import tools.artemis.systems.IteratingTaskSystem
import tools.eventbus.annotation.EventCallback

@All(Client::class)
class EventSystem: IteratingTaskSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var staticPositionMapper: ComponentMapper<StaticPosition>
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var sizeMapper: ComponentMapper<Size>

    override fun end() {
        getAddTasks().forEach { it.invoke() }
        clearTasks()
    }

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return
        for (entityId in client.getEntities()) {
            client.processEntityPosition(entityId)
        }
    }

    private fun Client.processEntityPosition(id: Int){
        val physics = physicsMapper[id]?: return
        val entityBody = physics.body?: return
        if (!entityBody.isActive) return
        addEvent(
            Event.Position(
                entityId = id,
                x = entityBody.position.x,
                y = entityBody.position.y
            )
        )
    }

    private fun Client.processEntity(id: Int){
        val entity = entityMapper[id]?: return
        addEvent(
            Event.Entity(
                entityId = id,
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

    @EventCallback
    fun showEntities(busEvent: BusEvent.ShowEntities){
        val client = clientMapper[busEvent.entityId]?: return
        client.addEntities(busEvent.entities)
        for (entityId in busEvent.entities) {
            addTask { client.processEntity(entityId) }
        }
    }

    @EventCallback
    fun hideEntities(busEvent: BusEvent.HideEntities) {
        val client = clientMapper[busEvent.entityId] ?: return
        client.removeEntities(busEvent.entities)
    }
}