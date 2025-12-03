package app.listeners.contact

import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import org.example.app.ecs.components.ContactItemsComponent
import org.example.core.models.box2d.FixtureType
import org.example.core.models.box2d.getFromContactAll

class ItemContactListener(private val artemisWorld: World): ContactListener {

    init { artemisWorld.inject(this) }

    private lateinit var contactItemsComponentMapper: ComponentMapper<ContactItemsComponent>

    override fun beginContact(contact: Contact) {
        val sensor = getFromContactAll<FixtureType.Sensor>(contact) ?: return
        val entity = getFromContactAll<FixtureType.Body>(contact) ?: return
        val contactItemsComponent = contactItemsComponentMapper[sensor.entityId]?: return
        contactItemsComponent.addItem(entity.entityId)
    }

    override fun endContact(contact: Contact) {
        val sensor = getFromContactAll<FixtureType.Sensor>(contact) ?: return
        val entity = getFromContactAll<FixtureType.Body>(contact) ?: return

        val contactItemsComponent = contactItemsComponentMapper[sensor.entityId]?: return
        contactItemsComponent.removeItem(entity.entityId)
    }

    override fun preSolve(contact: Contact, manifold: Manifold) {}

    override fun postSolve(
        contact: Contact,
        contactImpulse: ContactImpulse
    ) {}
}