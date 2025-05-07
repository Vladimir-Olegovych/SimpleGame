package org.example.models

sealed class FixtureType(val entityId: Int) {
    class Sensor(entityId: Int): FixtureType(entityId)
    class Entity(entityId: Int): FixtureType(entityId)
}