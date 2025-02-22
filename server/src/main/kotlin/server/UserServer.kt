package org.example.server

import client.models.ClientZombie
import client.models.User
import com.badlogic.gdx.utils.Disposable
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import kotlinx.coroutines.*

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
            when {
                obj is User -> println(obj.name)
            }
        }
    }

    fun start(){
        server = Server()
        val kryo = server.kryo
        kryo.register(Array<ClientZombie>::class.java)
        kryo.register(ClientZombie::class.java)
        kryo.register(User::class.java)
        server.bind(5000)
        server.addListener(connectionListener)
        server.start()
    }

    override fun dispose() {
        server.dispose()
    }

}