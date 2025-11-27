package org.example.core.items.manager

import com.badlogic.gdx.utils.IntMap
import org.example.core.items.WorldItem
import java.util.*

class ItemsManager {

    private var counterId = -1
    private val registeredIds = IdentityHashMap<Class<*>, Int>()
    private val factories = IntMap<(Int) -> WorldItem>()

    fun <T : WorldItem> create(type: Class<T>): T {
        val worldId = registeredIds[type]?: throw IllegalStateException("Item class not registered ${type.name}")
        val item = factories[worldId]?.invoke(worldId)!!
        item.onCreateItem()
        return item as T
    }

    fun <T : WorldItem> registerItem(type: Class<T>, factory: (Int) -> T) {
        if (registeredIds.containsKey(type)) return
        val worldId = ++counterId
        registeredIds[type] = worldId
        factories.put(worldId, factory)
    }

    fun <T : WorldItem> unregisterItem(type: Class<T>) {
        val worldId = registeredIds[type]?: return
        registeredIds.remove(type)
        factories.remove(worldId)
    }

}