package model

import type.EntityType


sealed class Event {

    class Entity(val entityId: Int = 0,
                 val entityType: EntityType = EntityType.NULL): Event()


    class Size(val entityId: Int = 0,
               val radius: Float = 0F,
               val halfWidth: Float = 0F,
               val halfHeight: Float = 0F): Event()

    class Position(val entityId: Int = 0,
                   val x: Float = 0F,
                   val y: Float = 0F): Event()

    class Remove(val entityId: Int = 0): Event()

    class CurrentChunkParams(val chunkRadius: Int = 0,
                             val chunkSize: Vector2Data = Vector2Data()): Event()

    class CurrentPlayer(val entityId: Int = 0): Event()

    class CurrentPlayerVelocity(val x: Float = 0F, val y: Float = 0F): Event()
}