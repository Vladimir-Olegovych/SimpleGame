package org.example.eventbus

import com.esotericsoftware.kryonet.Connection
import model.Event
import org.example.eventbus.event.BusEvent
import tools.eventbus.EventBus
import tools.kyro.common.GameNetworkListener

class ServerEventBus(): EventBus<BusEvent>() {

    private val listener = object : GameNetworkListener<Event>() {
        override fun onConnected(connection: Connection) {
            val clientEntityId = sendEvent(BusEvent.ProcessorEvent.OnConnected(connection))
        }

        override fun onDisconnected(connection: Connection) {
            sendEvent(BusEvent.ProcessorEvent.OnDisconnected(connection))
            connection.close()
        }

        override fun onReceive(connection: Connection, value: Event) {
            sendEvent(BusEvent.ProcessorEvent.OnReceive(connection, value))
        }

    }

    fun getListener(): GameNetworkListener<Event> = listener

}