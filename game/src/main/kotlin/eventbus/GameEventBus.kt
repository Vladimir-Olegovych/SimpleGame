package eventbus

import kotlinx.coroutines.CoroutineScope
import models.ServerWall
import models.ServerZombie
import org.example.tools.connection.ClientConnection
import org.example.tools.connection.listeners.ClientConnectionListener
import org.example.tools.connection.models.EntityEvent
import org.example.tools.connection.models.Event
import tools.eventbus.EventBus
import types.EventType

class GameEventBus(
    private val scope: CoroutineScope
): EventBus() {

    private val clientConnection = ClientConnection(object : ClientConnectionListener {
        override fun connected() {
            update(Event(EventType.CONNECTED.id))
        }

        override fun disconnected() {
            update(Event(EventType.DISCONNECTED.id))
        }

        override fun error(e: Throwable) {
            update(Event(EventType.ERROR.id))
        }

        override fun onEventPayload(event: Event) {
            update(event)
        }

    }, scope)

    fun connect(address: String, port: Int) {
        clientConnection.connect(
            address = address,
            port = port,
            registrations = arrayOf(
                Event::class.java,
                EntityEvent::class.java,
                ServerZombie::class.java,
                ServerWall::class.java,
            )
        )
    }

    fun close(){
        clientConnection.close()
    }
}