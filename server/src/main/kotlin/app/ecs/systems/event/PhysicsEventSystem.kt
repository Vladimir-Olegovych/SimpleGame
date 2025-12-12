package app.ecs.systems.event

import app.event.ChunkEvent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.ClientComponent
import event.Event
import models.network.SendType
import org.example.app.ecs.components.PhysicsComponent
import org.example.app.ecs.components.StaticAngleComponent
import org.example.app.ecs.components.StaticPositionComponent
import tools.eventbus.annotation.BusEvent


@All(ClientComponent::class)
class PhysicsEventSystem: IteratingSystem() {

    private lateinit var clientComponentMapper: ComponentMapper<ClientComponent>
    private lateinit var staticPositionComponentMapper: ComponentMapper<StaticPositionComponent>
    private lateinit var staticAngleComponentMapper: ComponentMapper<StaticAngleComponent>
    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>

    @BusEvent
    fun showEntities(event: ChunkEvent.Show){
        for (activatorId in event.activators){
            val clientComponent = clientComponentMapper[activatorId]?: continue
            for (entityId in event.entities) {
                staticPositionComponentMapper[entityId]?.position?.let {
                    clientComponent.addEvent(
                        Event.StaticPosition(
                            entityId = entityId,
                            x = it.x,
                            y = it.y,
                        )
                    )
                }
                staticAngleComponentMapper[entityId]?.angle?.let {
                    clientComponent.addEvent(
                        Event.StaticAngle(
                            entityId = entityId,
                            angle = it
                        )
                    )
                }

                physicsComponentMapper[entityId]?.let {
                    it.getBody()?: return@let
                    if (staticPositionComponentMapper[entityId] == null) {
                        clientComponent.addEvent(
                            Event.Position(
                                entityId = entityId,
                                x = it.positionUpdater.getAll().x,
                                y = it.positionUpdater.getAll().y
                            )
                        )
                        it.positionUpdater.markAsUpdated()
                    }
                    if (staticAngleComponentMapper[entityId] == null) {
                        clientComponent.addEvent(
                            Event.Angle(
                                entityId = entityId,
                                angle = it.angleUpdater.getAll()
                            )
                        )
                        it.angleUpdater.markAsUpdated()
                    }
                }
            }
        }
    }

    override fun process(entityId: Int) {
        val clientComponent = clientComponentMapper[entityId]?: return
        val entities = clientComponent.getEntities()
        for (entityId in entities) {
            val physicsComponent = physicsComponentMapper[entityId] ?: continue
            val entityBody = physicsComponent.getBody() ?: continue

            if (!entityBody.isActive || !entityBody.isAwake) continue
            if (staticPositionComponentMapper[entityId] == null &&
                physicsComponent.positionUpdater.hasUpdate()) {

                clientComponent.addEvent(
                    Event.Position(
                        entityId = entityId,
                        x = physicsComponent.positionUpdater.getUpdate().x,
                        y = physicsComponent.positionUpdater.getUpdate().y
                    ),
                    sendType = SendType.UDP
                )

            }

            if (staticAngleComponentMapper[entityId] == null &&
                physicsComponent.angleUpdater.hasUpdate()) {

                clientComponent.addEvent(
                    Event.Angle(
                        entityId = entityId,
                        angle = physicsComponent.angleUpdater.getUpdate()
                    ),
                    sendType = SendType.UDP
                )

            }
        }
    }

    override fun end() {
        for (i in 0 until subscription.entities.size()) {
            val clientId = subscription.entities[i]
            val client = clientComponentMapper[clientId]?: continue
            val entities = client.getEntities()

            fun finishProcess(entityId: Int) {
                physicsComponentMapper[entityId]?.let {
                    it.positionUpdater.markAsUpdated()
                    it.angleUpdater.markAsUpdated()
                }
            }

            entities.forEach { finishProcess(it) }
        }
    }

}