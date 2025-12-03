package app.di

import app.listeners.contact.ItemContactListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import core.physics.contact.ContactManager
import org.koin.dsl.module

val physicsModule = module {
    single<ContactManager> { ContactManager() }

    single<World> {
        val contactManager: ContactManager = get()
        World(Vector2(0F, 0F), false).apply {
            setContactListener(contactManager)
        }
    }

    single<ItemContactListener> {
        val artemisWorld: com.artemis.World = get()
        ItemContactListener(artemisWorld)
    }
}