package app.ecs.components

import app.ecs.components.sender.UpdatableData
import com.artemis.Component
import models.items.ItemContainer
import org.example.core.items.WorldItem
import kotlin.reflect.KClass

class InventoryComponent: Component() {

    var inventorySize = 32
    var canCollectItems = false
    private var hasUpdate = false

    private val inventorySlots: Array<Pair<KClass<out WorldItem>, Array<WorldItem>>?> = arrayOfNulls(inventorySize)

    fun addItem(worldItem: WorldItem): Boolean {
        val itemClass = worldItem::class

        for (i in inventorySlots.indices) {
            val slot = inventorySlots[i]
            if (slot != null) {
                val (slotClass, stack) = slot

                if (slotClass == itemClass && stack.size < worldItem.maxStack) {
                    inventorySlots[i] = Pair(slotClass, stack + worldItem)
                    hasUpdate = true
                    return true
                }
            }
        }

        for (i in inventorySlots.indices) {
            if (inventorySlots[i] == null) {
                inventorySlots[i] = Pair(itemClass, arrayOf(worldItem))
                hasUpdate = true
                return true
            }
        }

        return false
    }

    fun removeItem(slot: Int, count: Int = 1): Boolean {
        if (slot !in 0 until inventorySize) return false

        val slotData = inventorySlots[slot] ?: return false
        val stack = slotData.second

        if (stack.size < count) return false

        if (stack.size == count) {
            inventorySlots[slot] = null
        } else {
            val newStack = stack.sliceArray(0 until stack.size - count)
            inventorySlots[slot] = Pair(slotData.first, newStack)
        }
        hasUpdate = true
        return true
    }

    val inventoryUpdater = object : UpdatableData<Array<Array<ItemContainer>?>>() {
        override fun onHasUpdate(): Boolean {
            return hasUpdate
        }

        override fun onMarkAsUpdated() {
            hasUpdate = false
        }

        override fun getUpdate(): Array<Array<ItemContainer>?> {
            val array = arrayOfNulls<Array<ItemContainer>>(inventorySize)
            for (i in 0 until inventorySize){
                val slots = inventorySlots[i]?.second?: continue
                array[i] = slots.map { it.toItemContainer() }.toTypedArray()
            }
            return array
        }

        override fun getAll(): Array<Array<ItemContainer>?> {
            return getUpdate()
        }
    }
}