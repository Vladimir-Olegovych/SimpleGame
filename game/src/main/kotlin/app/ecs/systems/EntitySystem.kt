package app.ecs.systems

import app.ecs.components.EntityComponent
import app.ecs.models.Player
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem

@All(EntityComponent::class)
class EntitySystem(): IteratingSystem() {

    @Wire
    private lateinit var player: Player

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    override fun initialize() {
        player.entityId = world.create()
        entityComponentMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {

    }
}