package server

import com.esotericsoftware.kryonet.Connection

interface UserServerListener {
    fun onConnected(connection: Connection)
    fun onDisconnected(connection: Connection)
}