package org.example.ecs.features

import com.artemis.ComponentMapper
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import model.Event
import org.example.constants.WorldComponents
import org.example.ecs.components.Entity
import org.example.ecs.components.Player
import tools.artemis.features.Feature
import tools.physics.createCircleEntity

object PlayerFeature: Feature() {

    private val players = HashMap<Connection, Int>()

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var playerMapper: ComponentMapper<Player>

    fun getPlayers(): Map<Connection, Int> {
        return players
    }

    fun createPlayer(connection: Connection) = tasks.add {
        val entityId = artemisWorld.create()

        val client = clientMapper.create(entityId)
        val entity = entityMapper.create(entityId)
        val player = playerMapper.create(entityId)

        client.connection = connection
        WorldComponents.getBox2dWorld().createCircleEntity(
            x = 0F,
            y = 0F,
            restitution = 1F,
            radius = 1F,
            linearDamping = 0.1F,
            angularDamping = 0.1F
        ).let { body ->
            entity.body = body
            clientMapper[entityId].addEvent(Event.Player(entityId, body.position.x, body.position.y))
        }
        players[connection] = entityId
    }


    fun removePlayer(connection: Connection){
        val entityId = players[connection]?: return
        val entity = entityMapper[entityId]
        entity.body?.let { WorldComponents.getBox2dWorld().destroyBody(it) }

        clientMapper.remove(entityId)
        entityMapper.remove(entityId)
        playerMapper.remove(entityId)
        players.remove(connection)
    }

    override fun process(entityId: Int) {
        val player = playerMapper[entityId]?: return
        val entity = entityMapper[entityId]?: return
        val client = clientMapper[entityId]?: return
    }
}