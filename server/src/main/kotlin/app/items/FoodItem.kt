package org.example.app.items

import com.artemis.World
import org.example.app.ecs.utils.utCreateEntity
import org.example.core.items.WorldItem

class FoodItem(
    worldId: Int,
    private val artemisWorld: World,
): WorldItem(worldId) {
    override val name: String = "Food"

    override fun onCreateItem() {
        val entityId = artemisWorld.create()
        this.entityId = entityId
        artemisWorld.utCreateEntity(
            entityId = entityId,
            worldItem = this@FoodItem
        )
    }
}