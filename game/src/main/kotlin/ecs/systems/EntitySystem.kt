package ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.utils.IntMap
import core.models.ClientPreference
import ecs.components.*
import event.Event
import tools.eventbus.annotation.BusEvent
import values.ApplicationValues

@All(EntityModel::class)
class EntitySystem(): IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var clientPreference: ClientPreference

    private val entityMap = IntMap<Int>()
    private lateinit var entityMapper: ComponentMapper<EntityModel>
    private lateinit var entityStats: ComponentMapper<EntityStats>
    private lateinit var entityPositionMapper: ComponentMapper<EntityPosition>
    private lateinit var sizeMapper: ComponentMapper<Size>
    private lateinit var angleMapper: ComponentMapper<EntityAngle>

    @BusEvent
    fun setEntity(event: Event.Entity){
        val entity: EntityModel
        if (entityMap[event.entityId] == null) {
            val newId = world.create()
            entity = entityMapper.create(newId)
            entityMap.put(event.entityId, newId)
        } else {
            entity = entityMapper[entityMap[event.entityId]]
        }
        entity.isStatic = event.isStatic
        entity.drawStats = event.drawStats
        entity.entityType = event.entityType
        entity.textureType = event.textureType
    }

    @BusEvent
    fun setPosition(event: Event.Position){
        val entityId = entityMap[event.entityId]
        val entityPosition = entityPositionMapper[entityId]?: run {
            entityPositionMapper.create(entityId)
        }
        entityPosition.setPosition(
            event.x * clientPreference.drawScale,
            event.y * clientPreference.drawScale
        )
    }

    @BusEvent
    fun setSize(event: Event.Size){
        val entityId = entityMap[event.entityId]
        val size = sizeMapper[entityId]?: run {
            sizeMapper.create(entityId)
        }
        size.radius = event.radius * clientPreference.drawScale
        size.halfWidth = event.halfWidth * clientPreference.drawScale
        size.halfHeight = event.halfHeight * clientPreference.drawScale
    }

    @BusEvent
    fun setAngle(event: Event.Angle){
        val entityId = entityMap[event.entityId]
        val angle = angleMapper[entityId]?: run {
            angleMapper.create(entityId)
        }
        angle.setAngle(event.angle)
    }

    @BusEvent
    fun setStats(event: Event.Stats){
        val entityId = entityMap[event.entityId]
        val stats = entityStats[entityId]?: run {
            entityStats.create(entityId)
        }
        stats.setAllStats(event.stats)
    }

    private var maxDistance = Float.MAX_VALUE
    @BusEvent
    fun setChunkParams(event: Event.CurrentChunkParams){
        val chunkSize = clientPreference.drawScale * event.chunkSize
        maxDistance = (chunkSize * 4) * event.chunkRadius
    }

    @BusEvent
    fun setRemove(event: Event.Remove){
        val entityId = entityMap[event.entityId]?: return
        entityMap.remove(event.entityId)
        removeEntity(entityId)
    }

    @BusEvent
    fun setCurrentPlayer(event: Event.CurrentPlayer){
        player.serverId = event.entityId
        entityMap.put(event.entityId, player.entityId)
    }

    private fun removeEntity(entityId: Int) {
        entityMapper.remove(entityId)
        entityStats.remove(entityId)
        sizeMapper.remove(entityId)
        angleMapper.remove(entityId)
        entityPositionMapper.remove(entityId)
    }

    private fun processRemove(entityId: Int){
        if (player.entityId == entityId) return

        val entity = entityMapper[entityId]?: return

        val entityPosition = entityPositionMapper[entityId]?.getServerPosition()?: return
        val playerPosition = entityPositionMapper[player.entityId]?.getServerPosition()?: return

        val dx = entityPosition.x - playerPosition.x
        val dy = entityPosition.y - playerPosition.y
        val distanceSquared = dx * dx + dy * dy

        if (distanceSquared < maxDistance * maxDistance) return
        removeEntity(entityId)
    }

    override fun initialize() {
        player.entityId = world.create()
        entityMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {
        //processRemove(entityId)
    }


    override fun dispose() {


    }
}