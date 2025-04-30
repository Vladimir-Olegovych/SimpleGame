package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import tools.artemis.features.Feature

object SendFeature: Feature() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>

    override fun initialize() {

    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return

        for (i in 0 until clientIDs.entities.size()) {
            val client = clientMapper[clientIDs.entities[i]]?: continue
            val playerEntity = entityMapper[clientIDs.entities[i]]?: continue

            val playerEntityPosition = playerEntity.body?.position?: continue
            val entityPosition = entity.body?.position?: continue

            /*
            val rangeX = (playerEntityPosition.x - 20)..(playerEntityPosition.x + 120)
            val rangeY = (playerEntityPosition.y - 20)..(playerEntityPosition.y + 120)

            if (entityPosition.x !in rangeX || entityPosition.y !in rangeY) continue

             */
            client.addEvent(Event.Entity(entityId, entityPosition.x, entityPosition.y))
        }
    }
}