package app.di

import org.example.core.items.manager.ItemsManager
import org.koin.dsl.module

val itemModule = module {
    single<ItemsManager> { ItemsManager() }

}