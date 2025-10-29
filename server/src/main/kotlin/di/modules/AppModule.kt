package di.modules

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.World
import dagger.Module
import dagger.Provides
import event.Event
import org.example.core.models.ServerPreference
import org.example.ecs.processors.ClientProcessor
import org.example.ecs.systems.*
import tools.artemis.world.ArtemisWorldBuilder
import tools.kyro.server.GameServer
import tools.preference.JsonPreference
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideServerPreference(): ServerPreference {
        return JsonPreference(
            "server", ServerPreference()
        ).getPreference()
    }

    @Provides
    @Singleton
    fun provideGameServer(): GameServer<Event> {
        return GameServer()
    }

    @Provides
    @Singleton
    fun provideChunkManager(
        serverPreference: ServerPreference
    ): AdvancedChunkManager {
        val chunkManager = AdvancedChunkManager(
            visibleRadius = serverPreference.chunkRadius,
            chunkSize = serverPreference.chunkSize
        )
        return chunkManager
    }

    @Provides
    @Singleton
    fun provideArtemisWorld(
        serverPreference: ServerPreference,
        chunkManager: AdvancedChunkManager,
        clientProcessor: ClientProcessor,
        eventSystem: EventSystem,
        clientSystem: ClientSystem,
        moveSystem: MoveSystem,
        physicsSystem: PhysicsSystem,
        chunkSystem: ChunkSystem,
        entitySystem: EntitySystem
    ): World {
        val artemisWorld = ArtemisWorldBuilder()

            .addSystem(eventSystem)
            .addSystem(clientSystem)
            .addSystem(moveSystem)
            .addSystem(physicsSystem)
            .addSystem(chunkSystem)
            .addSystem(entitySystem)

            .addObject(clientProcessor.ClientProcessorContent())
            .addObject(serverPreference)
            .addObject(chunkManager)
            .build()
        return artemisWorld
    }

}