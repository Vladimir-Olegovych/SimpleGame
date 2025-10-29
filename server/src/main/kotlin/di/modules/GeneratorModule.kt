package org.example.di.modules

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.World
import dagger.Module
import dagger.Provides
import org.example.core.level.chunks.BlockChunkGenerator
import org.example.core.level.chunks.EntityChunkGenerator
import org.example.core.level.chunks.FloorChunkGenerator
import org.example.core.level.chunks.StructureChunkGenerator
import org.example.core.level.world.ServerWorldGenerator
import org.example.core.models.ServerPreference
import org.example.ecs.systems.ChunkSystem
import org.example.ecs.systems.EntitySystem
import org.example.ecs.systems.PhysicsSystem
import javax.inject.Singleton

@Module
class GeneratorModule {
    @Provides
    @Singleton
    fun provideFloorChunkGenerator(
        artemisWorld: World,
        entitySystem: EntitySystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem
    ): FloorChunkGenerator {
        return FloorChunkGenerator(
            artemisWorld = artemisWorld,
            entitySystem = entitySystem,
            physicsSystem = physicsSystem,
            chunkSystem = chunkSystem,
        )
    }

    @Provides
    @Singleton
    fun provideEntityChunkGenerator(
        artemisWorld: World,
        entitySystem: EntitySystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem
    ): EntityChunkGenerator {
        return EntityChunkGenerator(
            artemisWorld = artemisWorld,
            entitySystem = entitySystem,
            physicsSystem = physicsSystem,
            chunkSystem = chunkSystem,
        )
    }
    @Provides
    @Singleton
    fun provideBlockChunkGenerator(
        artemisWorld: World,
        entitySystem: EntitySystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem
    ): BlockChunkGenerator {
        return BlockChunkGenerator(
            artemisWorld = artemisWorld,
            entitySystem = entitySystem,
            physicsSystem = physicsSystem,
            chunkSystem = chunkSystem,
        )
    }

    @Provides
    @Singleton
    fun provideStructureChunkGenerator(
        artemisWorld: World,
        serverPreference: ServerPreference,
        chunkManager: AdvancedChunkManager,
        entitySystem: EntitySystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem
    ): StructureChunkGenerator {
        return StructureChunkGenerator(
            artemisWorld = artemisWorld,
            serverPreference = serverPreference,
            chunkManager = chunkManager,
            entitySystem = entitySystem,
            physicsSystem = physicsSystem,
            chunkSystem = chunkSystem,
        )
    }

    @Provides
    @Singleton
    fun provideServerWorldGenerator(
        serverPreference: ServerPreference,
        floorChunkGenerator: FloorChunkGenerator,
        entityChunkGenerator: EntityChunkGenerator,
        blockChunkGenerator: BlockChunkGenerator,
        structureChunkGenerator: StructureChunkGenerator,
    ): ServerWorldGenerator {
        return ServerWorldGenerator(
            serverPreference = serverPreference,
            generators = arrayOf(
                structureChunkGenerator,
                floorChunkGenerator,
                entityChunkGenerator,
                //blockChunkGenerator
            )
        )
    }
}