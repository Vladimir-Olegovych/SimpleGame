package org.example.ecs.features

import com.artemis.ComponentMapper
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import model.Event
import tools.artemis.features.Feature

object PlayerFeature: Feature() {

    private val players = HashMap<Connection, Int>()
    private lateinit var clientMapper: ComponentMapper<Client>

    fun getPlayers(): Map<Connection, Int> {
        return players
    }

    fun removePlayer(connection: Connection) {
        val entityId = players[connection]?: return
        val client = clientMapper[entityId]
        client.connection = null
        clientMapper.remove(entityId)
        players.remove(connection)

        for (playerConnection in players.keys) {
            playerConnection.sendTCP(Event.Remove(entityId))
        }
    }

    fun createPlayer(entityId: Int, connection: Connection) {
        players[connection] = entityId
        val client = clientMapper.create(entityId)
        client.connection = connection

        clientMapper[entityId].addEvent(
            Event.CurrentPlayer(
                entityId = entityId
            )
        )
    }

    override fun initialize() {}

    override fun process(entityId: Int) {}
}