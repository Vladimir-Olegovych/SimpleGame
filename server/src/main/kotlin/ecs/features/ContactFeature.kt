package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.*
import org.example.ecs.components.Entity
import tools.artemis.features.Feature

object ContactFeature: ContactListener, Feature() {

    @Wire private lateinit var box2dWold: World
    private lateinit var entityMapper: ComponentMapper<Entity>

    override fun beginContact(contact: Contact) {

    }

    override fun endContact(contact: Contact) {

    }

    override fun preSolve(contact: Contact, manifold: Manifold) {}
    override fun postSolve(contact: Contact, contactImpulse: ContactImpulse) {}


    override fun initialize() {
        box2dWold.setContactListener(this)
    }

    override fun process(entityId: Int) {}
}