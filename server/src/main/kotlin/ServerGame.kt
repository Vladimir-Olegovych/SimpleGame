import ecs.world.UltimateEcsWorld
import server.UserServer
import tools.render.LifecycleUpdater

class ServerGame: LifecycleUpdater(1F / 60F) {

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