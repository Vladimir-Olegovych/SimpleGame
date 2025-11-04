package event

import models.SendType
import models.StatContainer
import models.TextureType
import modificator.SendTypeModificator
import type.EntityType


class SendContainer<T>(val data: T,
                       override val sendType: SendType): SendTypeModificator

class GamePacket(val events: Array<Event> = emptyArray())

sealed class Event {

    class Entity(val entityId: Int = 0,
                 val drawStats: Boolean = true,
                 val isStatic: Boolean = false,
                 val textureType: TextureType = TextureType.NULL,
                 val entityType: EntityType = EntityType.NULL): Event()

    class Stats(
        val entityId: Int = 0,
        val stats: Array<StatContainer> = emptyArray()
    ): Event()

    class Size(val entityId: Int = 0,
               val radius: Float = 0F,
               val halfWidth: Float = 0F,
               val halfHeight: Float = 0F): Event()

    class Position(val entityId: Int = 0,
                   val x: Float = 0F,
                   val y: Float = 0F): Event()

    class Angle(val entityId: Int = 0,
                val angle: Float = 0F): Event()

    class Remove(val entityId: Int = 0): Event()


    class CurrentChunkParams(val chunkRadius: Int = 0,
                             val chunkSize: Float = 0F): Event()

    class CurrentPlayer(val entityId: Int = 0): Event()

    class CurrentPlayerVelocity(val x: Float = 0F,
                                val y: Float = 0F): Event()

}