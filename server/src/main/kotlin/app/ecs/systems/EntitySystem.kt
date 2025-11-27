package org.example.app.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import event.Event
import org.example.app.ecs.components.*
import org.example.core.models.server.EventContainer
import org.example.core.models.settings.ServerPreference
import tools.eventbus.annotation.BusEvent
import tools.eventbus.annotation.EventType

@All(EntityComponent::class)
class EntitySystem: IteratingSystem() {

    @Wire private lateinit var serverPreference: ServerPreference

    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var moveComponentMapper: ComponentMapper<MoveComponent>
    private lateinit var itemComponentMapper: ComponentMapper<ItemComponent>
    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var lookAtComponentMapper: ComponentMapper<LookAtComponent>

    @BusEvent
    @EventType(Event.CanCollectItems::class)
    fun onCanCollectItems(container: EventContainer<Event.CanCollectItems>) {
        val inventory = inventoryComponentMapper[container.entityId]?: return
        inventory.canCollectItems = container.event.value
    }

    @BusEvent
    @EventType(Event.LookAt::class)
    fun onLookAt(container: EventContainer<Event.LookAt>) {
        val lookAtComponent = lookAtComponentMapper[container.entityId]?.let {
            lookAtComponentMapper.create(container.entityId)
        }
        lookAtComponent?.lookAt = container.event.angle
    }

    @BusEvent
    @EventType(Event.CurrentPlayerVelocity::class)
    fun onCurrentPlayerVelocity(container: EventContainer<Event.CurrentPlayerVelocity>) {
        val move = moveComponentMapper[container.entityId]?: return
        val event = container.event
        val newVector = Vector2(0F, 0F)
        when {
            event.x > 0 -> newVector.x = serverPreference.maxPlayerSpeed
            event.x < 0 -> newVector.x = -serverPreference.maxPlayerSpeed
            else -> newVector.x = 0F
        }
        when {
            event.y > 0 -> newVector.y = serverPreference.maxPlayerSpeed
            event.y < 0 -> newVector.y = -serverPreference.maxPlayerSpeed
            else -> newVector.y = 0F
        }
        if (newVector.x == 0F && newVector.y == 0F) {
            move.vector = null
        } else {
            move.vector = newVector
        }
    }

    override fun process(entityId: Int) {}
}