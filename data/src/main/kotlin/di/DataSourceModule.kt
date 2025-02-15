package di

import client.datasource.ClientDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single<ClientDataSource> { ClientDataSource() }
}