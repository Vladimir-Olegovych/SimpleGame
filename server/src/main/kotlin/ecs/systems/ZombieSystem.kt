package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import ecs.components.Zombie
import models.ServerZombie
import types.EventType

@All(Zombie::class)
class ZombieSystem: IteratingSystem() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clients: ComponentMapper<Client>
    private lateinit var zombies: ComponentMapper<Zombie>

    override fun process(entityId: Int) {
        val zombie = zombies[entityId]

        for (i in 0 until clientIDs.entities.size()) {
            val client = clients[clientIDs.entities[i]]?: continue
            val serverZombie = ServerZombie(
                radius = zombie.radius,
                x = zombie.body?.position?.x?: 0F,
                y = zombie.body?.position?.y?: 0F
            )
            client.addEntityEvent(entityId, EventType.BODY, serverZombie)
        }
    }
}