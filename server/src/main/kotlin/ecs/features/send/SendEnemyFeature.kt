package org.example.ecs.features.send

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Enemy
import org.example.ecs.components.Entity
import tools.artemis.features.Feature

object SendEnemyFeature: Feature() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var enemyMapper: ComponentMapper<Enemy>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val enemy = enemyMapper[entityId]?: return
        val entity = entityMapper[entityId]?: return
        val entityPosition = entity.body?.position?: return

        for (i in 0 until clientIDs.entities.size()) {
            val client = clientMapper[clientIDs.entities[i]]?: continue
            client.addEvent(
                Event.Enemy(
                    entityId = entityId,
                    x = entityPosition.x,
                    y = entityPosition.y
                )
            )
        }
    }
}