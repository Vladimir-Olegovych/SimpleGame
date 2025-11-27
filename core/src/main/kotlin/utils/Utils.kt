package utils

import com.esotericsoftware.kryo.Kryo
import event.Event
import event.GamePacket
import models.ItemContainer
import models.StatContainer
import models.enums.EntityType
import models.enums.TextureType

fun Kryo.registerAllEvents(){
    val kryo = this

    kryo.register(ItemContainer::class.java)
    kryo.register(StatContainer::class.java)
    kryo.register(Event::class.java)
    kryo.register(Array<Event>::class.java)
    kryo.register(Array<StatContainer>::class.java)
    kryo.register(Array<ItemContainer>::class.java)
    kryo.register(GamePacket::class.java)

    kryo.register(TextureType::class.java)
    kryo.register(EntityType::class.java)

    kryo.register(Event.Entity::class.java)

    kryo.register(Event.Stats::class.java)

    kryo.register(Event.Size::class.java)
    kryo.register(Event.Angle::class.java)
    kryo.register(Event.Remove::class.java)
    kryo.register(Event.Texture::class.java)
    kryo.register(Event.Position::class.java)
    kryo.register(Event.Inventory::class.java)
    kryo.register(Event.EntityTypeEvent::class.java)

    kryo.register(Event.CurrentPlayer::class.java)
    kryo.register(Event.CurrentChunkParams::class.java)
    kryo.register(Event.CurrentPlayerVelocity::class.java)
    kryo.register(Event.CanCollectItems::class.java)
    kryo.register(Event.LookAt::class.java)
}