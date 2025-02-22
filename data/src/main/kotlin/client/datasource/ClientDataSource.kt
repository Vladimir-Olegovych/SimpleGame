package client.datasource

import client.datasource.listeners.OnClientReceive
import client.models.ClientZombie
import client.models.User
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener

class ClientDataSource {

    private var client: Client? = null
    private var listener: OnClientReceive? = null

    private val connectionListener = object : Listener() {
        override fun connected(connection: Connection?) {
            listener?.connected()
        }

        override fun disconnected(connection: Connection?) {
            connection?.close()
            listener?.disconnected()
        }
        override fun received(connection: Connection, obj: Any) {
            when (obj) {
                is Array<*> -> if (obj.isArrayOf<ClientZombie>()) {
                    listener?.receiveBody(obj as Array<ClientZombie>)
                }
            }
        }
    }

    fun setListener(listener: OnClientReceive){
        this.listener = listener
    }

    fun connect(ip: String, port: Int, user: User) {
        client?.let { return@connect }

        val client = Client()
        val kryo = client.kryo

        kryo.register(Array<ClientZombie>::class.java)
        kryo.register(ClientZombie::class.java)
        kryo.register(User::class.java)

        this.client = client

        client.addListener(connectionListener)

        client.start()

        try {
            client.connect(TIME_OUT, ip, port)
            client.sendTCP(user)
        } catch (e: Throwable) {
            listener?.error(e)
        }


    }

    fun disconnect() {
        client?.dispose()
        client = null
    }

    companion object {
        const val TIME_OUT = 5_000
    }
}