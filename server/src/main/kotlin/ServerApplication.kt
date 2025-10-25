package org.example

import com.artemis.World
import di.components.AppComponent
import di.components.DaggerAppComponent
import event.Event
import org.example.core.eventbus.ServerEventBus
import org.example.ecs.processors.ChunkProcessor
import org.example.ecs.processors.ClientProcessor
import org.example.ecs.systems.MoveSystem
import tools.graphics.render.LifecycleUpdater
import tools.kyro.server.GameServer
import utils.registerAllEvents
import java.util.concurrent.Executor
import javax.inject.Inject

class ServerApplication(
    private val port: Int = 5000
): LifecycleUpdater(1F / 30F), Executor {

    private lateinit var appComponent: AppComponent

    @Inject lateinit var artemisWorld: World
    @Inject lateinit var gameServer: GameServer<Event>
    @Inject lateinit var serverEventBus: ServerEventBus
    @Inject lateinit var clientProcessor: ClientProcessor
    @Inject lateinit var chunkProcessor: ChunkProcessor
    @Inject lateinit var moveSystem: MoveSystem

    override fun create() {
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)

        serverEventBus.addHandler(clientProcessor)
        serverEventBus.addHandler(chunkProcessor)
        serverEventBus.addHandler(moveSystem)

        gameServer.subscribe(serverEventBus.getListener())
        gameServer.start(
            port = port,
            bufferSize = 262144,
            custom = { kryo ->
                kryo.registerAllEvents()
            }
        )
    }

    override fun update(deltaTime: Float) {
        artemisWorld.delta = deltaTime
        try {
            artemisWorld.process()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun dispose() {
        serverEventBus.clearHandlers()
        gameServer.stop()
        artemisWorld.dispose()
    }
}