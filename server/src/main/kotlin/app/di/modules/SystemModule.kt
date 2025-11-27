package org.example.app.di.modules

import dagger.Module
import dagger.Provides
import org.example.app.ecs.systems.*
import javax.inject.Singleton

@Module
class SystemModule {

    @Provides
    @Singleton
    fun provideEventSystem(): EventSystem {
        return EventSystem()
    }

    @Provides
    @Singleton
    fun provideLookAtSystem(): LookAtSystem {
        return LookAtSystem()
    }

    @Provides
    @Singleton
    fun provideCollectItemsSystem(): CollectItemsSystem {
        return CollectItemsSystem()
    }

    @Provides
    @Singleton
    fun provideSendSystem(): SendSystem {
        return SendSystem()
    }

    @Provides
    @Singleton
    fun provideClientSystem(): ClientSystem {
        return ClientSystem()
    }

    @Provides
    @Singleton
    fun provideMoveSystem(): MoveSystem {
        return MoveSystem()
    }

    @Provides
    @Singleton
    fun providePhysicsSystem(): PhysicsSystem {
        return PhysicsSystem()
    }

    @Provides
    @Singleton
    fun provideChunkSystem(): ChunkSystem {
        return ChunkSystem()
    }

    @Provides
    @Singleton
    fun provideEntitySystem(): EntitySystem {
        return EntitySystem()
    }

}