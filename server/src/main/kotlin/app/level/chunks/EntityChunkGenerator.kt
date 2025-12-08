package org.example.app.level.chunks

import alexey.tools.server.level.AdvancedChunkManager
import app.items.DiamondItem
import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Vector2
import core.models.components.texture.TextureContainer
import models.entity.EntityType
import models.textures.TextureType
import org.example.app.ecs.components.EntityComponent
import org.example.app.ecs.components.SizeComponent
import org.example.app.ecs.utils.utCreateBody
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.items.manager.ItemsManager
import org.example.core.level.SingleChunkGenerator
import org.example.core.models.box2d.BodyType

class EntityChunkGenerator(
    private val artemisWorld: World,
    private val chunkManager: AdvancedChunkManager,
    private val itemsManager: ItemsManager
): SingleChunkGenerator() {

    init { artemisWorld.inject(this) }

    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>

    override fun generatePosition(position: Vector2): Boolean {
        if (getRandom().nextInt(0, 20) > 2) return false

        val entityId = artemisWorld.create()
        artemisWorld.utCreateEntity(
            entityId = entityId,
            texture = TextureContainer.get(TextureType.ITEM.DIAMOND),
            entityType = EntityType.ENTITY,
            isObserver = false,
            isPhysical = true,
            worldItem = itemsManager.create(DiamondItem::class.java),
        )
        sizeComponentMapper[entityId].let {
            it.radius = 0.2F
            it.width = 0.2F
            it.height = 0.2F
        }
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