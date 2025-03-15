package ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.IntMap
import ecs.components.Body
import ecs.components.Shape
import ecs.components.Square
import eventbus.GameEventBus
import models.ServerWall
import models.ServerZombie
import org.example.tools.connection.models.EntityEvent
import org.example.tools.connection.models.Event
import tools.eventbus.EventBus
import types.EventType

class ServerInputSystem: EventBus.Subscriber, BaseSystem() {

    @Wire private lateinit var eventBus: GameEventBus

    private lateinit var shapes: ComponentMapper<Shape>
    private lateinit var squares: ComponentMapper<Square>
    private lateinit var bodies: ComponentMapper<Body>

    private val bodyZombieMap = IntMap<Int>()
    private val bodyWallMap = IntMap<Int>()

    override fun initialize() {
        eventBus.subscribe(this)
    }

    override fun begin() {}
    override fun processSystem() {}

    override fun dispose() {
        eventBus.unSubscribe(this)
    }

    override fun acceptEvent(event: Event): Boolean {
        return event.eventId == EventType.BODY.id
    }

    override fun onEntityEventPayload(event: EntityEvent) {
        when(val content = event.content) {
            is ServerZombie -> updateShapes(event, content)
            is ServerWall -> updateSquares(event, content)
        }
    }
    private fun updateShapes(event: EntityEvent, content: ServerZombie){
        val bodyEntityId = bodyZombieMap[event.entityId]
        val body: Body
        val shape: Shape
        if (bodyEntityId != null) {
            body = bodies.get(bodyEntityId)
            shape = shapes.get(bodyEntityId)
        } else {
            val newBodyEntityId = world.create()
            body = bodies.create(newBodyEntityId)
            shape = shapes.create(newBodyEntityId)
            bodyZombieMap.put(event.entityId, newBodyEntityId)
        }
        body.x = content.x
        body.y = content.y
        shape.radius = content.radius
        shape.color = Color.GOLD
    }

    private fun updateSquares(event: EntityEvent, content: ServerWall){
        val bodyEntityId = bodyWallMap[event.entityId]
        val body: Body
        val square: Square
        if (bodyEntityId != null) {
            body = bodies.get(bodyEntityId)
            square = squares.get(bodyEntityId)
        } else {
            val newBodyEntityId = world.create()
            body = bodies.create(newBodyEntityId)
            square = squares.create(newBodyEntityId)
            bodyWallMap.put(event.entityId, newBodyEntityId)
        }
        body.x = content.x
        body.y = content.y
        square.halfWidth = content.halfWidth
        square.halfHeight = content.halfHeight
        square.color = Color.GOLD
    }
}