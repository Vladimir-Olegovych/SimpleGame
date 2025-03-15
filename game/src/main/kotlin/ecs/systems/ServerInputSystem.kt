package ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.IntMap
import ecs.components.Body
import ecs.components.Shape
import eventbus.GameEventBus
import models.PhysicalObject
import org.example.tools.connection.models.EntityEvent
import org.example.tools.connection.models.Event
import tools.eventbus.EventBus
import types.EventType

class ServerInputSystem: EventBus.Subscriber, BaseSystem() {

    @Wire private lateinit var eventBus: GameEventBus

    private lateinit var shapes: ComponentMapper<Shape>
    private lateinit var bodies: ComponentMapper<Body>

    private val bodyMap = IntMap<Int>()

    override fun initialize() {
        eventBus.subscribe(this)
    }

    override fun begin() {}
    override fun processSystem() {}

    override fun dispose() {
        eventBus.unSubscribe(this)
    }

    override fun acceptEvent(event: Event): Boolean {
        return event.eventId == EventType.BODY_PHYSICAL.id
    }

    override fun onEntityEventPayload(event: EntityEvent) {
        val serverBody = (event.content as? PhysicalObject)?: return

        val bodyEntityId = bodyMap[event.entityId]

        val body: Body
        val shape: Shape

        if (bodyEntityId != null) {
            body = bodies.get(bodyEntityId)
            shape = shapes.get(bodyEntityId)
        } else {
            val newBodyEntityId = world.create()
            body = bodies.create(newBodyEntityId)
            shape = shapes.create(newBodyEntityId)
            bodyMap.put(event.entityId, newBodyEntityId)
        }

        body.x = serverBody.x
        body.y = serverBody.y
        shape.radius = 1F
        shape.color = Color.GOLD
    }
}