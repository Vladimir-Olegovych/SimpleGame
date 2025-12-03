package app.di

import org.example.app.ecs.systems.*
import org.koin.dsl.module

val systemModule = module {
    single<EventSystem> { EventSystem() }
    single<LookAtSystem> { LookAtSystem() }
    single<CollectItemsSystem> { CollectItemsSystem() }
    single<SendSystem> { SendSystem() }
    single<ClientSystem> { ClientSystem() }
    single<MoveSystem> { MoveSystem() }
    single<PhysicsSystem> { PhysicsSystem() }
    single<ChunkSystem> { ChunkSystem() }
    single<EntitySystem> { EntitySystem() }
}