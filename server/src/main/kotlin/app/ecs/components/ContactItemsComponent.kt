package org.example.app.ecs.components

import com.artemis.Component
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class ContactItemsComponent: Component() {
    private val contactItems = ConcurrentLinkedQueue<Int>()

    fun addItem(entityId: Int) { contactItems.add(entityId) }

    fun removeItem(entityId: Int) { contactItems.add(entityId) }

    fun getItems(): Queue<Int> = contactItems

    fun clearItems() { contactItems.clear() }
}