package org.example.ecs.features

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import ecs.components.Client
import model.Event
import org.example.ecs.components.Entity
import org.example.ecs.components.Radius
import org.example.ecs.components.Size
import tools.artemis.features.Feature

object EventFeature: Feature() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private lateinit var entityMapper: ComponentMapper<Entity>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var radiusMapper: ComponentMapper<Radius>

    override fun initialize() {}

    override fun process(entityId: Int) {
        val client = clientMapper[entityId]?: return
        val clientEntity = entityMapper[entityId]?: return

        var size = 0
        WorldFeature.getAllChunksInRadius(clientEntity.chunkPosition) { chunk ->
            size++
            for(id in chunk.getEntities()){
                client.processEntityOnChunk(id)
            }
        }
    }

    private fun Client.processEntityOnChunk(id: Int){
        val entity = entityMapper[id]?: return
        val entityBody = entity.body?: return

        if (this.ownedEntity[id] == null) {
            this.addEvent(
                Event.Entity(
                    entityId = id,
                    entityType = entity.entityType
                )
            )
            this.ownedEntity.put(id, 0)
        }

        radiusMapper[id]?.let {
            this.addEvent(
                Event.Radius(
                    entityId = id,
                    radius = it.radius
                )
            )
        }
        sizeMapper[id]?.let {
            this.addEvent(
                Event.Size(
                    entityId = id,
                    halfHeight = it.halfHeight,
                    halfWidth = it.halfWidth
                )
            )
        }

        this.addEvent(
            Event.Position(
                entityId = id,
                x = entityBody.position.x,
                y = entityBody.position.y
            )
        )
    }
}