package org.example.ecs.features

import com.artemis.ComponentMapper
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.Player
import tools.artemis.features.Feature

object PlayerFeature: Feature() {

    private val players = HashMap<Connection, Int>()
    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var playerMapper: ComponentMapper<Player>

    fun getPlayers(): Map<Connection, Int> {
        return players
    }

    fun removePlayer(connection: Connection) {
        val entityId = players[connection]?: return
        clientMapper.remove(entityId)
        players.remove(connection)

        for (playerConnection in players.keys) {
            playerConnection.sendTCP(Event.PlayerDisconnected(entityId))
        }
    }

    fun createPlayer(entityId: Int, connection: Connection) = tasks.add {
        players[connection] = entityId
        val client = clientMapper.create(entityId)
        client.connection = connection

        val entity = entityMapper[entityId]
        val player = playerMapper[entityId]

        val position = entity.body?.position?: return@add

        clientMapper[entityId].addEvent(
            Event.Player(
                entityId, position.x, position.y
            )
        )
    }

    override fun initialize() {}

    override fun process(entityId: Int) {
        val player = playerMapper[entityId]?: return
        val entity = entityMapper[entityId]?: return
        val client = clientMapper[entityId]?: return
    }
}