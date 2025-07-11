package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import model.Event
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Size

@All(Client::class)
class EventSystem: IteratingSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var sizeMapper: ComponentMapper<Size>

    @All(EntityModel::class) private lateinit var entityIds: EntitySubscription

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return

        for (i in 0 until entityIds.entities.size()) {
            val entityId = entityIds.entities.get(i)
            client.processEntity(entityId)
            client.processEntityPosition(entityId)
        }
    }

    private fun Client.processEntityPosition(id: Int){
        val entity = entityMapper[id]?: return
        val entityBody = entity.body?: return
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
}