package ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.utils.IntMap
import ecs.components.Entity
import ecs.components.Player
import model.Event
import tools.kyro.client.GameClient
import tools.kyro.common.GameNetworkListener

class ServerInputSystem: BaseSystem() {

    @Wire private lateinit var gameClient: GameClient<Event>

    private val listeners = ArrayList<GameNetworkListener<Event>>()
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var playerMapper: ComponentMapper<Player>
    private val entityMap = IntMap<Int>()

    override fun initialize() {
        val entityListener = gameClient.subscribe<Event.Entity>(
            onReceive = { listener, connection, data ->
                val entity: Entity

                if (entityMap[data.entityId] == null) {
                    val newId = world.create()
                    entity = entityMapper.create(newId)
                    entityMap.put(data.entityId, newId)
                } else {
                    entity = entityMapper[entityMap[data.entityId]]
                }

                entity.x = data.x
                entity.y = data.y
            }
        )
        val playerListener = gameClient.subscribe<Event.Player>(
            onReceive = { listener, connection, data ->
                val newId = world.create()
                val entity = entityMapper.create(newId)
                val player = playerMapper.create(newId)

                player.serverId = data.entityId
                entity.x = data.x
                entity.y = data.y

                entityMap.put(data.entityId, newId)
            }
        )
        listeners.add(entityListener as GameNetworkListener<Event>)
        listeners.add(playerListener as GameNetworkListener<Event>)
    }
    override fun processSystem() {}

    override fun dispose() {
        listeners.forEach { gameClient.unSubscribe(it) }
    }
}