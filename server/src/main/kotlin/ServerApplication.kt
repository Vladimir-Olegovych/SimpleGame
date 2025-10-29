package org.example

import com.artemis.World
import di.components.AppComponent
import di.components.DaggerAppComponent
import event.Event
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
    @Inject lateinit var clientProcessor: ClientProcessor
    @Inject lateinit var chunkProcessor: ChunkProcessor
    @Inject lateinit var moveSystem: MoveSystem

    override fun create() {
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)
        appComponent.inject(clientProcessor)

        gameServer.subscribe(clientProcessor)
        gameServer.subscribe(moveSystem)

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
        gameServer.stop()
        artemisWorld.dispose()
    }
}