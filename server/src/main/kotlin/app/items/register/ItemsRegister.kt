package app.items.register

import app.items.DiamondItem
import org.example.app.items.IronBarItem
import org.example.core.items.manager.ItemsManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ItemsRegister: KoinComponent {

    //private val artemisWorld: World by inject()
    private val itemsManager: ItemsManager by inject()

    fun initialize() {
        itemsManager.apply {
            registerItem(DiamondItem::class.java) { DiamondItem(it) }
            registerItem(IronBarItem::class.java) { IronBarItem(it) }
        }
    }
}