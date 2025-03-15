package server

import com.badlogic.gdx.utils.Disposable
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import models.PhysicalObject
import org.example.tools.connection.models.EntityEvent
import org.example.tools.connection.models.Event

class UserServer(private val userServerListener: UserServerListener): Disposable {

    private lateinit var server: Server

    private val connectionListener = object : Listener() {
        override fun connected(connection: Connection) {
            userServerListener.onConnected(connection)
        }
        override fun disconnected(connection: Connection) {
            userServerListener.onDisconnected(connection)
            connection.close()
        }
        override fun received(connection: Connection, obj: Any) {

        }
    }

    fun start(){
        server = Server(8192, 4096)
        val kryo = server.kryo
        kryo.register(Event::class.java)
        kryo.register(EntityEvent::class.java)
        kryo.register(PhysicalObject::class.java)
        server.bind(5000)
        server.addListener(connectionListener)
        server.start()
    }

    override fun dispose() {
        server.dispose()
    }

}