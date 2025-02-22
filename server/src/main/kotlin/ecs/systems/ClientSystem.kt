package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.ecs.components.Client

@All(Client::class)
class ClientSystem: IteratingSystem() {

    private lateinit var clients: ComponentMapper<Client>

    override fun process(entityId: Int) {
        val client = clients[entityId]
        client.events?.forEach { client.connection?.sendTCP(it) }
    }

}