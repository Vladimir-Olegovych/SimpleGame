package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client

@All(Client::class)
class ClientInputSystem: IteratingSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]

        client.getEvents().forEach { client.connection?.sendTCP(it) }
        client.clearEvents()
    }
}

