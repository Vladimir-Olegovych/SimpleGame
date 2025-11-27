package org.example.app.level.chunks

import alexey.tools.server.level.AdvancedChunkManager
import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import models.enums.EntityType
import models.enums.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.utils.utCreateBody
import org.example.app.ecs.utils.utCreateEntity
import org.example.app.items.FoodItem
import org.example.app.items.GunItem
import org.example.core.items.manager.ItemsManager
import org.example.core.level.SingleChunkGenerator
import org.example.core.models.box2d.BodyType
import values.ApplicationValues

class EntityChunkGenerator(
    private val artemisWorld: World,
    private val chunkManager: AdvancedChunkManager,
    private val itemsManager: ItemsManager
): SingleChunkGenerator() {

    init {
        itemsManager.apply {
            registerItem(GunItem::class.java)  {  GunItem(it, artemisWorld) }
            registerItem(FoodItem::class.java) { FoodItem(it, artemisWorld) }
        }
    }

    init { artemisWorld.inject(this) }

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>

    private val entityStats = mapOf<String, Any>(
        ApplicationValues.Stats.NAME to "Name",
        ApplicationValues.Stats.HP to 2000
    )

    override fun generatePosition(position: Vector2): Boolean {
        if (getRandom().nextInt(0, 20) > 2) return false

        val entityId = artemisWorld.create()
        artemisWorld.utCreateEntity(
            entityId = entityId,
            textureType = TextureType.ZOMBIE,
            entityType = EntityType.ENTITY,
            isObserver = false,
            isPhysical = true,
            worldItem = itemsManager.create(FoodItem::class.java),
            entityStats = entityStats
        )
        artemisWorld.utCreateBody(
            entityId = entityId,
            vector2 = position,
            bodyType = BodyType.CIRCLE,
            isEnabled = false
        )
        val entityComponent = entityComponentMapper[entityId]
        getChunk().add(entityId, entityComponent.isObserver)
        return true
    }

}