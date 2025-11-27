package di.modules

import alexey.tools.server.level.AdvancedChunkManager
import com.badlogic.gdx.physics.box2d.World
import dagger.Module
import dagger.Provides
import event.GamePacket
import org.example.app.ecs.systems.*
import org.example.app.level.generator.ServerWorldGenerator
import org.example.core.items.manager.ItemsManager
import org.example.core.models.settings.ServerPreference
import tools.artemis.world.ArtemisWorldBuilder
import tools.eventbus.EventBus
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
    fun provideEventBus(): EventBus = EventBus()


    @Provides
    @Singleton
    fun provideGameServer(): GameServer<GamePacket> {
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
        /* Components */
        world: World,
        eventBus: EventBus,
        itemsManager: ItemsManager,
        serverPreference: ServerPreference,
        chunkManager: AdvancedChunkManager,
        /* Systems */
        chunkSystem: ChunkSystem, clientSystem: ClientSystem, collectItemsSystem: CollectItemsSystem,
        entitySystem: EntitySystem, eventSystem: EventSystem, lookAtSystem: LookAtSystem,
        moveSystem: MoveSystem, physicsSystem: PhysicsSystem, sendSystem: SendSystem
    ) = ArtemisWorldBuilder().let {
        it.addSystem(clientSystem)
        it.addSystem(chunkSystem)
        it.addSystem(entitySystem)
        it.addSystem(moveSystem)
        it.addSystem(lookAtSystem)
        it.addSystem(collectItemsSystem)
        it.addSystem(physicsSystem)
        it.addSystem(eventSystem)
        it.addSystem(sendSystem)

        it.addObject(serverPreference)
        it.addObject(chunkManager)
        it.addObject(itemsManager)
        it.addObject(eventBus)
        it.addObject(world)

        it.build()
    }

}