package utils

import com.esotericsoftware.kryo.Kryo
import model.Event

fun Kryo.registerAllEvents(){
    val kryo = this
    kryo.register(Event::class.java)
    kryo.register(Event.Wall::class.java)
    kryo.register(Event.Entity::class.java)
    kryo.register(Event.Player::class.java)
    kryo.register(Event.PlayerVelocity::class.java)
    kryo.register(Event.PlayerDisconnected::class.java)
}