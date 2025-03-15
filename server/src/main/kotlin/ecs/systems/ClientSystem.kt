package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import ecs.components.Physical
import models.PhysicalObject
import types.EventType

@All(Physical::class)
class ClientSystem: IteratingSystem() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clients: ComponentMapper<Client>
    private lateinit var physicals: ComponentMapper<Physical>

    override fun process(entityId: Int) {
        val physical = physicals[entityId]

        for (i in 0 until clientIDs.entities.size()) {
            val client = clients[clientIDs.entities[i]]?: continue
            val physicalObject = PhysicalObject(
                x = physical.body?.position?.x?: 0F,
                y = physical.body?.position?.y?: 0F
            )
            client.addEntityEvent(entityId, EventType.BODY_PHYSICAL, physicalObject)
        }
    }
}