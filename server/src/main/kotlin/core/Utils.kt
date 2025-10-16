package org.example.core

import com.badlogic.gdx.physics.box2d.Fixture
import org.example.core.models.FixtureType

inline fun <reified T> Iterable<*>.getType(): T {
    for (element in this) {
        if (element is T) {
            return element
        }
    }
    throw NullPointerException("${T::class.java} not found in ${this::class.java}")
}

inline fun <reified T> Array<*>.getType(): T {
    for (element in this) {
        if (element is T) {
            return element
        }
    }
    throw NullPointerException("${T::class.java} not found in ${this::class.java}")
}

fun getFixtureSensor(fixture: Fixture): FixtureType.Sensor? {
    return fixture.userData as? FixtureType.Sensor
}

fun getFixtureEntity(fixture: Fixture): FixtureType.Entity? {
    return fixture.userData as? FixtureType.Entity
}