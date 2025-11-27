package org.example.core.physics.contact

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class ContactManager: ContactListener {

    private val listeners = ArrayList<ContactListener>()

    fun addListener(contactListener: ContactListener){
        listeners.add(contactListener)
    }

    fun removeListener(contactListener: ContactListener){
        listeners.remove(contactListener)
    }

    override fun beginContact(contact: Contact) {
        listeners.forEach { it.beginContact(contact) }
    }

    override fun endContact(contact: Contact) {
        listeners.forEach { it.endContact(contact) }
    }

    override fun preSolve(contact: Contact, manifold: Manifold) {
        listeners.forEach { it.preSolve(contact, manifold) }
    }

    override fun postSolve(
        contact: Contact,
        contactImpulse: ContactImpulse
    ) {
        listeners.forEach { it.postSolve(contact, contactImpulse) }
    }

}