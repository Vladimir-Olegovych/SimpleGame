package ecs.systems

import client.models.ClientZombie
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.badlogic.gdx.utils.IntMap
import ecs.components.Zombie
import eventbus.Events
import eventbus.GameEventBus
import tools.eventbus.EventBus

@All(Zombie::class)
class ZombieSystem: EventBus.SubscribeEventPayload<Array<ClientZombie>>, BaseSystem() {

    @Wire private lateinit var eventBus: GameEventBus
    private lateinit var zombies: ComponentMapper<Zombie>
    private val zombieMap = IntMap<Int>()


    override fun initialize() {
        eventBus.subscribe(Events.ZOMBIE, this)
    }

    override fun processSystem() {

    }

    override fun onEventPayload(event: Array<ClientZombie>) {
        for (serverZombie in event){
            val zombieEntityId = zombieMap[serverZombie.entityId]
            if (zombieEntityId != null) {
                val zombie = zombies.get(zombieEntityId)
                zombie.apply {
                    zombie.x = serverZombie.x
                    zombie.y = serverZombie.y
                    zombie.radius = serverZombie.radius
                }
            } else {
                val newZombieEntityId = world.create()
                val newZombie = zombies.create(newZombieEntityId)
                newZombie.apply {
                    x = serverZombie.x
                    y = serverZombie.y
                    radius = serverZombie.radius
                }
                zombieMap.put(serverZombie.entityId, newZombieEntityId)
            }
        }
    }

    override fun dispose() {
        eventBus.unSubscribe(this)
    }
}