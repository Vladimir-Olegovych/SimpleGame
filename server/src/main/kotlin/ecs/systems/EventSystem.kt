package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
@All(Client::class)
class EventSystem: IteratingSystem() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription

    private lateinit var clients: ComponentMapper<Client>

    override fun begin() {

    }

    override fun process(entityId: Int) {
        val client = clients[entityId]

        client.getQueue().forEach { client.connection?.sendTCP(it) }
        client.getEntityQueue().forEach { client.connection?.sendTCP(it) }

        client.getQueue().clear()
        client.getEntityQueue().clear()
    }


}