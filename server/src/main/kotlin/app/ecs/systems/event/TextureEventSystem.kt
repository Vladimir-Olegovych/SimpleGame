package app.ecs.systems.event

import app.ecs.components.VisibleComponent
import app.event.ChunkEvent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event
import org.example.app.ecs.components.TextureComponent
import tools.eventbus.annotation.BusEvent

@All(TextureComponent::class, VisibleComponent::class)
class TextureEventSystem: IteratingSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>

    @BusEvent
    fun showEntities(event: ChunkEvent.Show){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            for (entityId in event.entities) {
                val textureComponent = textureComponentMapper[entityId]?: continue
                clientComponent.addEvent(Event.Texture(
                    entityId = entityId,
                    textureId = textureComponent.texture?.textureId?: continue
                ))
            }
        }
    }

    override fun process(entityId: Int) {}

}