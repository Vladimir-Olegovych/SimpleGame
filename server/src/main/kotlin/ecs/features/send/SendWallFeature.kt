package org.example.ecs.features.send

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.Wall
import tools.artemis.features.Feature

object SendWallFeature: Feature() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var wallMapper: ComponentMapper<Wall>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val wall = wallMapper[entityId]?: return
        val entity = entityMapper[entityId]?: return
        val entityPosition = entity.body?.position?: return

        for (i in 0 until clientIDs.entities.size()) {
            val client = clientMapper[clientIDs.entities[i]]?: continue
            if (client.wallMap[entityId] != null) continue
            client.wallMap[entityId] = 0
            client.addEvent(
                Event.Wall(
                    entityId = entityId,
                    x = entityPosition.x,
                    y = entityPosition.y,
                    halfWidth = wall.halfWidth,
                    halfHeight = wall.halfHeight
                )
            )
        }
    }
}