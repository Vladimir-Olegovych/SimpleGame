package app.di

import app.ecs.systems.TimeSystem
import app.ecs.systems.event.*
import org.example.app.ecs.systems.*
import org.koin.dsl.module

val systemModule = module {
    single<TimeSystem> { TimeSystem() }
    single<LookAtSystem> { LookAtSystem() }
    single<CollectItemsSystem> { CollectItemsSystem() }
    single<SendSystem> { SendSystem() }
    single<ClientSystem> { ClientSystem() }
    single<MoveSystem> { MoveSystem() }
    single<PhysicsSystem> { PhysicsSystem() }
    single<ChunkSystem> { ChunkSystem() }
    single<EntitySystem> { EntitySystem() }


    single<EntityEventSystem> { EntityEventSystem() }
    single<EntityTypeEventSystem> { EntityTypeEventSystem() }
    single<TextureEventSystem> { TextureEventSystem() }
    single<SizeEventSystem> { SizeEventSystem() }
    single<PhysicsEventSystem> { PhysicsEventSystem() }
    single<InventoryEventSystem> { InventoryEventSystem() }
    single<StatsEventSystem> { StatsEventSystem() }
    single<TimeEventSystem> { TimeEventSystem() }
}