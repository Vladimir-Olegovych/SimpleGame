package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.Size
import tools.artemis.features.Feature

object SendEntityFeature: Feature() {

    @All(Entity::class) private lateinit var entityIds: EntitySubscription
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var sizeMapper: ComponentMapper<Size>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return

        for (i in 0 until entityIds.entities.size()) {
            val id = entityIds.entities[i]
            val entity = entityMapper[id]?: continue
            val entityBody = entity.body?: continue

            if (client.hasEntities[id] == null) {
                client.addEvent(
                    Event.Entity(
                        entityId = id,
                        entityType = entity.entityType
                    )
                )
                client.hasEntities.put(id, 0)
            }

            client.addEvent(
                Event.Position(
                    entityId = id,
                    x = entityBody.position.x,
                    y = entityBody.position.y
                )
            )

            sizeMapper[id]?.let { sizes ->
                client.addEvent(
                    Event.Size(
                        entityId = id,
                        halfHeight = sizes.halfHeight,
                        halfWidth = sizes.halfWidth
                    )
                )
            }
        }
    }
}