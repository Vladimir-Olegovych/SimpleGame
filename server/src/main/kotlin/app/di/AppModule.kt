package app.di

import alexey.tools.server.level.AdvancedChunkManager
import app.ecs.components.models.ServerTime
import app.ecs.systems.TimeSystem
import app.ecs.systems.event.EntityEventSystem
import app.ecs.systems.event.EntityTypeEventSystem
import app.ecs.systems.event.InventoryEventSystem
import app.ecs.systems.event.PhysicsEventSystem
import app.ecs.systems.event.SizeEventSystem
import app.ecs.systems.event.StatsEventSystem
import app.ecs.systems.event.TextureEventSystem
import app.ecs.systems.event.TimeEventSystem
import com.badlogic.gdx.physics.box2d.World
import event.GamePacket
import org.example.app.ecs.systems.*
import org.example.core.items.manager.ItemsManager
import org.example.core.models.settings.ServerPreference
import org.koin.dsl.module
import tools.artemis.world.ArtemisWorldBuilder
import tools.eventbus.EventBus
import tools.kyro.server.GameServer
import tools.preference.JsonPreference

val appModule = module {
    single<ServerPreference> {
        JsonPreference("server", ServerPreference()).getPreference()
    }

    single<EventBus> { EventBus() }

    single<GameServer<GamePacket>> { GameServer() }

    single<AdvancedChunkManager> {
        val serverPreference: ServerPreference = get()
        AdvancedChunkManager(
            visibleRadius = serverPreference.chunkRadius,
            chunkSize = serverPreference.chunkSize
        )
    }

    single<com.artemis.World> {
        val world: World = get()
        val eventBus: EventBus = get()
        val itemsManager: ItemsManager = get()
        val serverPreference: ServerPreference = get()
        val chunkManager: AdvancedChunkManager = get()

        val chunkSystem: ChunkSystem = get()
        val clientSystem: ClientSystem = get()
        val collectItemsSystem: CollectItemsSystem = get()
        val entitySystem: EntitySystem = get()
        val lookAtSystem: LookAtSystem = get()
        val moveSystem: MoveSystem = get()
        val physicsSystem: PhysicsSystem = get()
        val sendSystem: SendSystem = get()
        val timeSystem: TimeSystem = get()
        val entityEventSystem: EntityEventSystem = get()
        val entityTypeEventSystem: EntityTypeEventSystem = get()
        val textureEventSystem: TextureEventSystem = get()
        val sizeEventSystem: SizeEventSystem = get()
        val physicsEventSystem: PhysicsEventSystem = get()
        val inventoryEventSystem: InventoryEventSystem = get()
        val statsEventSystem: StatsEventSystem = get()
        val timeEventSystem: TimeEventSystem = get()

        ArtemisWorldBuilder().let {
            it.addSystem(clientSystem)
            it.addSystem(sendSystem)

            it.addSystem(timeSystem)
            it.addSystem(entitySystem)
            it.addSystem(moveSystem)
            it.addSystem(lookAtSystem)
            it.addSystem(collectItemsSystem)

            it.addSystem(physicsSystem)
            it.addSystem(chunkSystem)

            it.addSystem(entityEventSystem)
            it.addSystem(entityTypeEventSystem)
            it.addSystem(textureEventSystem)
            it.addSystem(sizeEventSystem)
            it.addSystem(physicsEventSystem)
            it.addSystem(inventoryEventSystem)
            it.addSystem(statsEventSystem)
            it.addSystem(timeEventSystem)

            it.addObject(ServerTime())
            it.addObject(serverPreference)
            it.addObject(chunkManager)
            it.addObject(itemsManager)
            it.addObject(eventBus)
            it.addObject(world)

            it.build()
        }
    }
}
