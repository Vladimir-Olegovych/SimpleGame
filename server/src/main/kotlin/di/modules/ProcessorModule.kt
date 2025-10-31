package org.example.di.modules

import alexey.tools.server.level.AdvancedChunkManager
import dagger.Module
import dagger.Provides
import org.example.core.level.world.ServerWorldGenerator
import org.example.ecs.processors.ChunkProcessor
import org.example.ecs.processors.ClientProcessor
import org.example.ecs.systems.*
import javax.inject.Singleton

@Module
class ProcessorModule {

    @Provides
    @Singleton
    fun provideClientProcessor(
        clientSystem: ClientSystem,
        entitySystem: EntitySystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem,
    ): ClientProcessor {
        return ClientProcessor(
            clientSystem = clientSystem,
            entitySystem = entitySystem,
            physicsSystem = physicsSystem,
            chunkSystem = chunkSystem
        )
    }

    @Provides
    @Singleton
    fun provideChunkProcessor(
        chunkManager: AdvancedChunkManager,
        physicsSystem: PhysicsSystem,
        eventSystem: EventSystem,
        chunkGenerator: ServerWorldGenerator
    ): ChunkProcessor {
        return ChunkProcessor(
            physicsSystem = physicsSystem,
            eventSystem = eventSystem,
            chunkGenerator = chunkGenerator
        ).apply {
            chunkManager.putListener(this)
        }
    }
}