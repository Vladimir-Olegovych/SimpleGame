package org.example.app.di.modules

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import dagger.Module
import dagger.Provides
import org.example.app.listeners.contact.ItemContactListener
import org.example.core.physics.contact.ContactManager
import javax.inject.Singleton

@Module
class PhysicsModule {

    @Provides
    @Singleton
    fun provideBox2dWorld(contactManager: ContactManager): World {
        return World(Vector2(0F, 0F), false).apply {
            setContactListener(contactManager)
        }
    }

    @Provides
    @Singleton
    fun provideItemContactListener(artemisWorld: com.artemis.World): ItemContactListener {
        return ItemContactListener(artemisWorld)
    }

    @Provides
    @Singleton
    fun provideContactManager(): ContactManager {
        return ContactManager()
    }

}