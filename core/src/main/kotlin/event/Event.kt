package event

import models.entity.EntityType
import models.items.ItemContainer
import models.network.SendType
import models.stats.StatContainer
import modificator.SendTypeModificator


class SendContainer<T>(val data: T, override val sendType: SendType): SendTypeModificator

class GamePacket(val events: Array<Event> = emptyArray())

sealed class Event {

    class Time(
        val time: Float = 0F
    ): Event()

    class Entity(val entityId: Int = 0): Event()

    class Stats(
        val entityId: Int = 0,
        val stats: Array<StatContainer> = emptyArray()
    ): Event()

    class Texture(
        val entityId: Int = 0,
        val textureId: Int = 0
    ): Event()

    class EntityTypeEvent(
        val entityId: Int = 0,
        val entityType: EntityType = EntityType.NULL
    ): Event()

    class Inventory(
        val entityId: Int = 0,
        val inventory: Array<Array<ItemContainer>?> = emptyArray()
    ): Event()

    class Size(val entityId: Int = 0,
               val radius: Float = 0F,
               val width: Float = 0F,
               val height: Float = 0F): Event()

    class Position(val entityId: Int = 0,
                   val x: Float = 0F,
                   val y: Float = 0F): Event()

    class StaticPosition(val entityId: Int = 0,
                         val x: Float = 0F,
                         val y: Float = 0F): Event()


    class Angle(val entityId: Int = 0,
                val angle: Float = 0F): Event()

    class StaticAngle(val entityId: Int = 0,
                      val angle: Float = 0F): Event()

    class Remove(val entityId: Int = 0): Event()


    class ServerParams(val dayTime: Float = 0F,
                       val eveningTime: Float = 0F,
                       val nightTime: Float = 0F,
                       val dawnTime: Float = 0F): Event()

    class CurrentPlayer(val entityId: Int = 0): Event()

    class CanCollectItems(val value: Boolean = false): Event()

    class LookAt(val angle: Float = 0F): Event()

    class CurrentPlayerVelocity(
        val x: Float = 0F,
        val y: Float = 0F
    ): Event()
    
}