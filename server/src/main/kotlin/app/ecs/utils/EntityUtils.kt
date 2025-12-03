package org.example.app.ecs.utils

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import core.models.components.texture.TextureContainer
import models.entity.EntityType
import org.example.app.ecs.components.*
import org.example.core.items.WorldItem
import org.example.core.models.settings.ServerPreference

fun World.utCreateEntity(entityId: Int,
                         entityType: EntityType? = null,
                         texture: TextureContainer? = null,
                         isObserver: Boolean = false,
                         isPhysical: Boolean = false,
                         staticPosition: Vector2? = null,
                         worldItem: WorldItem? = null,
                         drawStats: Boolean = true,
                         canCollectItems: Boolean = true,
                         entityStats: Map<String, Any>? = null,
                         hasInventory: Boolean = false,
                         inventoryItems: List<WorldItem> = listOf()) {
    val serverPreference = this.getRegistered(ServerPreference::class.java)

    val textureComponentMapper = this.getMapper(TextureComponent::class.java)
    val entityTypeComponentMapper = this.getMapper(EntityTypeComponent::class.java)
    val itemComponentMapper = this.getMapper(ItemComponent::class.java)
    val staticPositionComponentMapper = this.getMapper(StaticPositionComponent::class.java)
    val entityComponentMapper = this.getMapper(EntityComponent::class.java)
    val statsComponentMapper = this.getMapper(StatsComponent::class.java)
    val physicsComponentMapper = this.getMapper(PhysicsComponent::class.java)
    val sizeComponentMapper = this.getMapper(SizeComponent::class.java)
    val inventoryComponentMapper = this.getMapper(InventoryComponent::class.java)
    val contactItemsComponentMapper = this.getMapper(ContactItemsComponent::class.java)

    val entity = entityComponentMapper.create(entityId)
    entity.isObserver = isObserver
    entity.drawStats = drawStats

    if (entityType != null) {
        val etComponent = entityTypeComponentMapper.create(entityId)
        etComponent.entityType = entityType
    }

    if (texture != null) {
        val size = sizeComponentMapper.create(entityId)
        val halfSize = serverPreference.blockSize / 2F
        size.radius = halfSize
        size.halfWidth = halfSize
        size.halfHeight = halfSize

        val tComponent = textureComponentMapper.create(entityId)
        tComponent.texture = texture
    }

    if (isPhysical) {
        physicsComponentMapper.create(entityId)
    }

    if (staticPosition != null) {
        val staticPositionComponent = staticPositionComponentMapper.create(entityId)
        staticPositionComponent.position = staticPosition
    }

    if (entityStats != null) {
        val statsComponent = statsComponentMapper.create(entityId)
        statsComponent.setAllStats(entityStats)
    }

    if(worldItem != null){
        worldItem.entityId = entityId
        val itemComponent = itemComponentMapper.create(entityId)
        itemComponent.worldItem = worldItem
    }

    if (hasInventory || inventoryItems.isNotEmpty()) {
        contactItemsComponentMapper.create(entityId)
        val inventoryComponent = inventoryComponentMapper.create(entityId)
        inventoryComponent.canCollectItems = canCollectItems
        for (item in inventoryItems) {
            inventoryComponent.addItem(item)
        }
    }
}

fun World.utRemoveEntity(entityId: Int) {
    this.delete(entityId)
}