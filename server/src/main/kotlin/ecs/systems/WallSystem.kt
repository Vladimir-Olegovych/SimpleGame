package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import ecs.components.Wall
import models.ServerWall
import types.EventType

@All(Wall::class)
class WallSystem: IteratingSystem() {

    @All(Client::class) private lateinit var clientIDs: EntitySubscription
    private lateinit var clients: ComponentMapper<Client>
    private lateinit var walls: ComponentMapper<Wall>

    override fun process(entityId: Int) {
        val wall = walls[entityId]

        for (i in 0 until clientIDs.entities.size()) {
            val client = clients[clientIDs.entities[i]]?: continue
            if(client.connectionWallMap.get(entityId) == HAS_WALL) continue
            client.connectionWallMap.put(entityId, HAS_WALL)

            val serverWall = ServerWall(
                x = wall.body?.position?.x?: 0F,
                y = wall.body?.position?.y?: 0F,
                halfWidth = wall.halfWidth,
                halfHeight = wall.halfHeight
            )
            client.addEntityEvent(entityId, EventType.BODY, serverWall)
        }
    }

    companion object {
        const val HAS_WALL = 1
    }
}