package org.example.core.models.box2d

import com.badlogic.gdx.physics.box2d.Contact

sealed class FixtureType(val entityId: Int) {
    class Body(entityId: Int): FixtureType(entityId)
    class Sensor(entityId: Int): FixtureType(entityId)
}

inline fun <reified T: FixtureType>getFromContactAll(contact: Contact): T? {
    val dataA = contact.fixtureA.userData
    val dataB = contact.fixtureB.userData
    return when {
        dataA is T -> dataA
        dataB is T -> dataB
        else -> null
    }
}

inline fun <reified T: FixtureType>getFromContactA(contact: Contact): T? {
    val dataA = contact.fixtureA.userData
    return when {
        dataA is T -> dataA
        else -> null
    }
}inline fun <reified T: FixtureType>getFromContactB(contact: Contact): T? {
    val dataB = contact.fixtureB.userData
    return when {
        dataB is T -> dataB
        else -> null
    }
}