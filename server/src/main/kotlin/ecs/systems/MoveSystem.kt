package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import model.Event
import org.example.ecs.components.EntityModel
import org.example.ecs.components.Move
import org.example.core.eventbus.event.BusEvent
import org.example.core.models.ServerPreference
import tools.eventbus.annotation.EventCallback
import tools.eventbus.annotation.EventType

@All(EntityModel::class)
class MoveSystem: IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var moveMapper: ComponentMapper<Move>

    @EventType(BusEvent.FIELD_EVENT)
    @EventCallback
    private fun applyForceToBody(busEvent: BusEvent.OnReceiveId<Event.CurrentPlayerVelocity>){
        val move = moveMapper[busEvent.entityId]?: return
        when {
            busEvent.event.x > 0 -> move.vector.x = serverPreference.maxSpeed
            busEvent.event.x < 0 -> move.vector.x = -serverPreference.maxSpeed
            else -> move.vector.x = 0F
        }
        when {
            busEvent.event.y > 0 -> move.vector.y = serverPreference.maxSpeed
            busEvent.event.y < 0 -> move.vector.y = -serverPreference.maxSpeed
            else -> move.vector.y = 0F
        }
    }

    override fun process(entityId: Int) {
        val entity = entityMapper[entityId]?: return
        val move = moveMapper[entityId]?: return

        val body = entity.body?: return
        body.applyForce(move.vector, body.worldCenter, true)
    }
}