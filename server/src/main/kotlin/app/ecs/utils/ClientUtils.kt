package org.example.app.ecs.utils

import com.artemis.World
import com.esotericsoftware.kryonet.Connection
import ecs.components.ClientComponent
import event.Event
import org.example.core.models.settings.ServerPreference

fun World.utRemoveClient(entityId: Int){
    val clientComponentMapper = this.getMapper(ClientComponent::class.java)

    val client = clientComponentMapper[entityId]
    client.dispose()
    clientComponentMapper.remove(entityId)
}

fun World.utCreateClient(entityId: Int, connection: Connection) {
    val serverPreference = this.getRegistered(ServerPreference::class.java)

    val clientComponentMapper = this.getMapper(ClientComponent::class.java)
    val client = clientComponentMapper.create(entityId)
    client.connection = connection

    client.addEvent(
        Event.ServerParams(
            dayTime = serverPreference.dayTime,
            eveningTime = serverPreference.eveningTime,
            nightTime = serverPreference.nightTime,
            dawnTime = serverPreference.dawnTime
        )
    )
    client.addEvent(
        Event.CurrentPlayer(
            entityId = entityId
        )
    )
}