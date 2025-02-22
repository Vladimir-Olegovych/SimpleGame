package org.example.ecs.systems

import client.models.ClientZombie
import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import org.example.ecs.components.Client
import org.example.ecs.components.Physical
import org.example.ecs.components.Zombie

@All(Client::class)
class EventSystem: IteratingSystem() {

    private lateinit var clients: ComponentMapper<Client>
    private lateinit var zombies: ComponentMapper<Zombie>
    private lateinit var physical: ComponentMapper<Physical>

    override fun process(entityId: Int) {
        val client = clients[entityId]
        val events = ArrayList<Any>()

        if (client.isNewConnection){
            client.isNewConnection = false
        }

        events.add(getZombieArray())

        client.events = events.toArray()
    }


    private fun getZombieArray(): Array<ClientZombie> {
        val aspect = Aspect.all(Physical::class.java, Zombie::class.java)
        val subscription = world.aspectSubscriptionManager.get(aspect)
        val entities = subscription.entities

        val array = Array(entities.size()) {
            val entityId = entities[it]
            val physical = physical.get(entityId)
            val zombie = zombies.get(entityId)
            return@Array ClientZombie(
                entityId = entityId,
                x = physical.body?.position?.x?: 0F,
                y = physical.body?.position?.y?: 0F,
                radius = zombie.radius
            )
        }
        return array
    }
}