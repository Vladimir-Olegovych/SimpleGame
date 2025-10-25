package org.example.core

import alexey.tools.common.math.ImmutableIntVector2
import com.badlogic.gdx.math.Vector2
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

fun getWorldPosition(intVector2: ImmutableIntVector2, chunkSize: Float): Vector2 {

    fun fixCoordinate(coord: Int): Float {
        return if (coord < 0) (coord + 1) * chunkSize - chunkSize else coord * chunkSize
    }

    return Vector2(fixCoordinate(intVector2.x), fixCoordinate(intVector2.y))
}