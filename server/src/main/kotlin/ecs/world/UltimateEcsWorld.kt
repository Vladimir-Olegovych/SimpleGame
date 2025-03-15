package ecs.world

import com.artemis.Aspect
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import ecs.systems.ZombieSystem
import ecs.systems.EventSystem
import ecs.systems.PhysicSystem
import ecs.systems.WallSystem
import server.UserServerListener
import tools.artemis.world.ArtemisWorldBuilder

class UltimateEcsWorld: UserServerListener {
    private val world = ArtemisWorldBuilder.Builder()
    .setSystemArray(arrayOf(PhysicSystem(), ZombieSystem(), WallSystem(), EventSystem()))
    .build()

    private val clientMapper = world.getMapper(Client::class.java)

    fun update(deltaTime: Float){
        world.delta = deltaTime
        world.process()
    }

    fun dispose(){
        world.dispose()
    }

    override fun onConnected(connection: Connection) {
        val client = clientMapper.create(world.create())
        client.connection = connection
    }

    override fun onDisconnected(connection: Connection) {
        val aspect = Aspect.all(Client::class.java)
        val subscription = world.aspectSubscriptionManager.get(aspect)
        val entities = subscription.entities
        for (i in 0 until  entities.size()){
            val entityId = entities[i]
            val client = clientMapper.get(entityId)
            if (client.connection == connection) {
                clientMapper.remove(entityId)
                break
            }
        }
    }

}