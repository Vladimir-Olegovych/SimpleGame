package client.repository

import client.datasource.ClientDataSource
import client.datasource.repository.ClientRepository

class ClientRepositoryImpl(
    private val clientDataSource: ClientDataSource
): ClientRepository {
}