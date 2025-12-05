package org.example.app.items

import models.textures.TextureType
import models.textures.asTextureId
import org.example.core.items.WorldItem

class IronBarItem(
    worldId: Int
): WorldItem(worldId) {

    override val name: String = "Iron Bar"
    override val textureId: Int = TextureType.ITEM.IRON_BAR.asTextureId()

    override fun onCreateItem() {

    }
}