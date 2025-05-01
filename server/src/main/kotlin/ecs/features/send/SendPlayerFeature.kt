package org.example.ecs.features.send

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.game.Enemy
import org.example.ecs.components.game.Player
import tools.artemis.features.Feature

object SendPlayerFeature: Feature() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var playerMapper: ComponentMapper<Player>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val player = playerMapper[entityId]?: return
        val entity = entityMapper[entityId]

        for (i in 0 until clientIDs.entities.size()) {
            val client = clientMapper[clientIDs.entities[i]]?: continue
            val entityPosition = entity.body?.position?: continue

            client.addEvent(Event.Enemy(entityId, entityPosition.x, entityPosition.y))
        }
    }
}