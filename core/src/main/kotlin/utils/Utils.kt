package utils

import com.esotericsoftware.kryo.Kryo
import model.Event
import model.Vector2Data
import type.EntityType

fun Kryo.registerAllEvents(){
    val kryo = this

    kryo.register(Vector2Data::class.java)

    kryo.register(Event::class.java)
    kryo.register(EntityType::class.java)

    kryo.register(Event.Entity::class.java)

    kryo.register(Event.Size::class.java)
    kryo.register(Event.Remove::class.java)
    kryo.register(Event.Position::class.java)

    kryo.register(Event.CurrentPlayer::class.java)
    kryo.register(Event.CurrentChunkParams::class.java)
    kryo.register(Event.CurrentPlayerVelocity::class.java)
}