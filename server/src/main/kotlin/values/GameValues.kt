package org.example.values

import com.esotericsoftware.kryonet.Connection
import org.example.models.ServerPreference

object GameValues {
    private var serverPreference: ServerPreference? = null
    val playersMap = HashMap<Connection, Int>()

    fun getServerPreference(): ServerPreference = serverPreference!!

    fun setServerPreference(serverPreference: ServerPreference) {
        this.serverPreference = serverPreference
    }
}