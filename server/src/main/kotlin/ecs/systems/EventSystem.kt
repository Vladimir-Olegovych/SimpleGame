package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Size
import org.example.core.eventbus.event.BusEvent
import tools.artemis.systems.IteratingTaskSystem
import tools.eventbus.annotation.EventCallback

@All(Client::class)
class EventSystem: IteratingTaskSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>

    override fun begin() {
        getAddTasks().forEach { it.invoke() }
        clearTasks()
    }

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return

        for (chunk in client.getChunks()) {
            for (entityId in chunk.getEntities()) {
                client.processEntityPosition(entityId)
            }
        }
    }

    private fun Client.processEntityPosition(id: Int){
        val entity = entityMapper[id]?: return
        val entityBody = entity.body?: return
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
    }

    @EventCallback
    private fun entityMovedOnChunk(busEvent: BusEvent.EntityMovedOnChunk){
        for (observerId in busEvent.chunk.getObservers()){
            if (busEvent.entityId == observerId) continue
            val client = clientMapper[observerId]?: continue
            addTask { client.processEntity(busEvent.entityId) }
        }
    }

    @EventCallback
    private fun loadChunks(busEvent: BusEvent.LoadChunks){
        val client = clientMapper[busEvent.entityId]?: return
        for (chunk in busEvent.chunks) {
            client.addChunk(chunk)
            for (entityId in chunk.getEntities()) {
                addTask { client.processEntity(entityId) }
            }
        }
    }

    @EventCallback
    private fun unloadChunks(busEvent: BusEvent.UnloadChunks) {
        val client = clientMapper[busEvent.entityId] ?: return
        for (chunk in busEvent.chunks) {
            client.removeChunk(chunk)
        }
    }
}