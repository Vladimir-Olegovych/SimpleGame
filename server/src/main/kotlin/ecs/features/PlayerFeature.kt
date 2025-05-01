package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import com.esotericsoftware.kryonet.Connection
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.Player
import tools.artemis.features.Feature
import tools.physics.createCircleEntity

object PlayerFeature: Feature() {

    private val players = HashMap<Connection, Int>()

    @Wire private lateinit var box2dWold: World

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
        box2dWold.createCircleEntity(
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


    fun removePlayer(connection: Connection) = tasks.add {
        val playerId = players[connection] ?: return@add
        val entity = entityMapper[playerId]
        entity.body?.let { box2dWold.destroyBody(it) }
        entity.body = null

        clientMapper.remove(playerId)
        entityMapper.remove(playerId)
        playerMapper.remove(playerId)
        players.remove(connection)

        for (playerConnection in players.keys) {
            playerConnection.sendTCP(Event.PlayerDisconnected(playerId))
        }
    }

    override fun initialize() {}

    override fun process(entityId: Int) {
        val player = playerMapper[entityId]?: return
        val entity = entityMapper[entityId]?: return
        val client = clientMapper[entityId]?: return
    }
}