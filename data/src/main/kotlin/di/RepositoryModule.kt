package di

import client.datasource.ClientDataSource
import client.datasource.repository.ClientRepository
import client.repository.ClientRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ClientRepository> {
        ClientRepositoryImpl(
            clientDataSource = get<ClientDataSource>()
        )
    }
}