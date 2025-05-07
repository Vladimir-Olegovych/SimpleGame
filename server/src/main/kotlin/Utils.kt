package org.example

import com.badlogic.gdx.physics.box2d.Fixture
import org.example.models.FixtureType

fun getFixtureSensor(fixture: Fixture): FixtureType.Sensor? {
    return fixture.userData as? FixtureType.Sensor
}

fun getFixtureEntity(fixture: Fixture): FixtureType.Entity? {
    return fixture.userData as? FixtureType.Entity
}