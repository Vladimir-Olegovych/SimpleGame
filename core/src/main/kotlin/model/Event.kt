package model


sealed class Event {

    open class Entity(val entityId: Int = 0,
                 val x: Float = 0F,
                 val y: Float = 0F): Event()

    class Player(entityId: Int = 0,
                 x: Float = 0F,
                 y: Float = 0F): Entity(entityId, x, y)

    class Wall(entityId: Int = 0,
               x: Float = 0F,
               y: Float = 0F): Entity(entityId, x, y)

    class PlayerDisconnected(val entityId: Int = 0): Event()

    class PlayerVelocity(val x: Float = 0F, val y: Float = 0F): Event()
}