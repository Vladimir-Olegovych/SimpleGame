package di

import org.koin.dsl.koinApplication

val koinApp = koinApplication {
    modules(appModule, dataSourceModule, repositoryModule)
}