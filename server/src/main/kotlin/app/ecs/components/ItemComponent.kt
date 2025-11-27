package org.example.app.ecs.components

import com.artemis.Component
import org.example.core.items.WorldItem

class ItemComponent: Component() {
    var worldItem: WorldItem? = null
}