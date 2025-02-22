package org.example

import org.example.ecs.world.UltimateEcsWorld
import org.example.server.UserServer
import tools.render.LifecycleUpdater

class ServerGame: LifecycleUpdater() {

    private val ultimateEcsWorld = UltimateEcsWorld()
    private val userServer = UserServer(ultimateEcsWorld)

    init { userServer.start() }

    override fun update(deltaTime: Float) {
        ultimateEcsWorld.update(deltaTime)

    }

    override fun dispose() {
        userServer.dispose()
        ultimateEcsWorld.dispose()
    }

}