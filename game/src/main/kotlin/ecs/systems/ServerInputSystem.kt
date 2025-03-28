package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
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
import java.time.Duration
import java.time.Instant

@All(Body::class)
class ServerInputSystem: EventBus.Subscriber, IteratingSystem() {

    @Wire private lateinit var eventBus: GameEventBus

    private lateinit var shapes: ComponentMapper<Shape>
    private lateinit var squares: ComponentMapper<Square>
    private lateinit var bodies: ComponentMapper<Body>

    private val bodyZombieMap = IntMap<Int>()
    private val bodyWallMap = IntMap<Int>()

    override fun process(entityId: Int) {
        val body = bodies[entityId]
        //interpolate(body)
    }

    override fun initialize() {
        eventBus.subscribe(this)
    }


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
        body.renderPosition = Vector2(content.x, content.y)
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
        body.renderPosition = Vector2(content.x, content.y)
        square.halfWidth = content.halfWidth
        square.halfHeight = content.halfHeight
        square.color = Color.GRAY
    }

    private fun interpolate(body: Body) {
        if (!body.hasInterpolationData()) return

        val (oldPos, oldTime) = body.serverPositions[0]
        val (newPos, newTime) = body.serverPositions[1]
        val now = Instant.now()

        val duration = Duration.between(oldTime, newTime).toMillis().toFloat()
        val elapsed = Duration.between(oldTime, now).toMillis().toFloat().coerceIn(0f, duration)

        val t = if (duration > 0) elapsed / duration else 0f
        body.renderPosition = oldPos.lerp(newPos, t)
    }
}