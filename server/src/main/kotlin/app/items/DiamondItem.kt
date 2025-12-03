package app.items

import org.example.core.items.WorldItem

class DiamondItem(
    worldId: Int
): WorldItem(worldId) {
    
    override val name: String = "Diamond"

    override fun onCreateItem() {

    }
}