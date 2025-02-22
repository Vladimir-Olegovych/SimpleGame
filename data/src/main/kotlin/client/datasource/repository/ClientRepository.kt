package client.datasource.repository

import client.datasource.listeners.OnClientReceive
import client.models.User

interface ClientRepository {
    fun setListener(listener: OnClientReceive)
    fun connect(ip: String, port: Int, user: User)
    fun disconnect()
}

