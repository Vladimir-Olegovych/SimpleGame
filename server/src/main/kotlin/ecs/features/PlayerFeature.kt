package org.example.ecs.features

import com.artemis.ComponentMapper
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import org.example.values.GameValues.playersMap
import model.Event
import tools.artemis.features.Feature

object PlayerFeature: Feature() {

    private lateinit var clientMapper: ComponentMapper<Client>

    fun removePlayer(connection: Connection) {
        val entityId = playersMap[connection]?: return
        val client = clientMapper[entityId]
        client.connection = null
        clientMapper.remove(entityId)
        playersMap.remove(connection)

        for (playerConnection in playersMap.keys) {
            playerConnection.sendTCP(Event.Remove(entityId))
        }
    }

    fun createPlayer(entityId: Int, connection: Connection) {
        playersMap[connection] = entityId
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