package org.example.core.models

sealed class FixtureType(val entityId: Int) {
    class Sensor(entityId: Int): FixtureType(entityId)
    class Entity(entityId: Int): FixtureType(entityId)
}