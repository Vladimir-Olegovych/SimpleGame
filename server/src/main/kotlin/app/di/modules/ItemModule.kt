package org.example.app.di.modules

import dagger.Module
import dagger.Provides
import org.example.core.items.manager.ItemsManager
import javax.inject.Singleton

@Module
class ItemModule {

    @Provides
    @Singleton
    fun provideItemsManager(): ItemsManager {
        return ItemsManager()
    }

}