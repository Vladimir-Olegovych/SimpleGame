package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.esotericsoftware.kryonet.Connection
import event.Event
import org.example.core.models.ServerPreference
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Move
import org.example.ecs.components.Physics
import org.example.ecs.processors.ClientProcessor
import tools.kyro.common.GameNetworkListener

@All(EntityModel::class)
class MoveSystem: GameNetworkListener<Event.CurrentPlayerVelocity>, IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    @Wire private lateinit var clientProcessorContent: ClientProcessor.ClientProcessorContent
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var physicsMapper: ComponentMapper<Physics>
    private lateinit var moveMapper: ComponentMapper<Move>

    override fun onReceive(connection: Connection, value: Event.CurrentPlayerVelocity) {
        val entityId = clientProcessorContent.getPlayers()[connection]?: return
        val move = moveMapper[entityId]?: return
        when {
            value.x > 0 -> move.vector.x = serverPreference.maxPlayerSpeed
            value.x < 0 -> move.vector.x = -serverPreference.maxPlayerSpeed
            else -> move.vector.x = 0F
        }
        when {
            value.y > 0 -> move.vector.y = serverPreference.maxPlayerSpeed
            value.y < 0 -> move.vector.y = -serverPreference.maxPlayerSpeed
            else -> move.vector.y = 0F
        }
    }

    override fun process(entityId: Int) {
        val physics = physicsMapper[entityId]?: return
        val move = moveMapper[entityId]?: return

        val body = physics.body?: return
        body.applyForce(move.vector, body.worldCenter, true)
    }
}