package app.ecs.systems.event

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event
import org.example.app.ecs.components.StatsComponent

@All(ClientComponent::class, StatsComponent::class)
class StatsEventSystem: IteratingSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>

    override fun inserted(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        val statsComponent = statsComponentMapper[entityId]?: return
        statsComponent.statsUpdater.markAsUpdated()
        val stats = statsComponent.statsUpdater.getAll()
        if (stats.isEmpty()) return
        clientComponent.addEvent(
            Event.Stats(
                entityId = entityId,
                stats = stats,
            )
        )
    }

    override fun process(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        val statsComponent = statsComponentMapper[entityId]?: return

        if (!statsComponent.statsUpdater.hasUpdate()) return
        val stats = statsComponent.statsUpdater.getUpdate()
        if (stats.isEmpty()) return
        clientComponent.addEvent(
            Event.Stats(
                entityId = entityId,
                stats = stats,
            )
        )
        statsComponent.statsUpdater.markAsUpdated()
    }

}