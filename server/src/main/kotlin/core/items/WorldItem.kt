package org.example.core.items

import models.items.ItemContainer
import models.textures.TextureType
import models.textures.asTextureId

open class WorldItem(val worldId: Int) {
    var entityId = -1
    open val maxStack = 64
    open val textureId = TextureType.BLOCK.ERROR.asTextureId()
    open val name: String = "Unknown Item"
    open val description: String = "No description available"
    open fun onCreateItem(){}

    fun toItemContainer(): ItemContainer {
        return ItemContainer(
            entityId = this.entityId,
            worldId = this.worldId,
            textureId = this.textureId,
            name = this.name,
            description = this.description
        )
    }
}