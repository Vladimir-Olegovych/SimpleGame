package client.repository

import client.datasource.ClientDataSource
import client.datasource.listeners.OnClientReceive
import client.datasource.repository.ClientRepository
import client.models.User

class ClientRepositoryImpl(
    private val clientDataSource: ClientDataSource
): ClientRepository {
    override fun setListener(listener: OnClientReceive) {
        clientDataSource.setListener(listener)
    }

    override fun connect(ip: String, port: Int, user: User) {
        clientDataSource.connect(ip, port, user)
    }

    override fun disconnect() {
        clientDataSource.disconnect()
    }
}