package org.example.core.items

open class WorldItem(val worldId: Int) {
    var entityId = -1
    open val name: String = "Unknown Item"
    open val description: String = "No description available"
    open fun onCreateItem(){}
}