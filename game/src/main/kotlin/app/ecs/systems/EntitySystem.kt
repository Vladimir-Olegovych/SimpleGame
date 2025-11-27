package app.ecs.systems

import app.ecs.components.*
import app.ecs.models.Player
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.utils.IntMap
import core.models.settings.ClientPreference
import event.Event
import tools.eventbus.annotation.BusEvent

@All(EntityComponent::class)
class EntitySystem(): IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var clientPreference: ClientPreference

    private val entityMap = IntMap<Int>()
    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>

    @BusEvent
    fun setEntity(event: Event.Entity){
        val entity: EntityComponent
        if (entityMap[event.entityId] == null) {
            val newId = world.create()
            entity = entityComponentMapper.create(newId)
            entityMap.put(event.entityId, newId)
        } else {
            entity = entityComponentMapper[entityMap[event.entityId]]
        }
        entity.isStatic = event.isStatic
        entity.drawStats = event.drawStats
    }

    @BusEvent
    fun setPosition(event: Event.Position){
        val entityId = entityMap[event.entityId]
        val entityPosition = positionComponentMapper[entityId]?: run {
            positionComponentMapper.create(entityId)
        }
        entityPosition.setPosition(
            event.x * clientPreference.drawScale,
            event.y * clientPreference.drawScale
        )
    }

    @BusEvent
    fun setSize(event: Event.Size){
        val entityId = entityMap[event.entityId]
        val size = sizeComponentMapper[entityId]?: run {
            sizeComponentMapper.create(entityId)
        }
        size.radius = event.radius * clientPreference.drawScale
        size.halfWidth = event.halfWidth * clientPreference.drawScale
        size.halfHeight = event.halfHeight * clientPreference.drawScale
    }

    @BusEvent
    fun setAngle(event: Event.Angle){
        val entityId = entityMap[event.entityId]
        val angle = angleComponentMapper[entityId]?: run {
            angleComponentMapper.create(entityId)
        }
        angle.setAngle(event.angle)
    }

    @BusEvent
    fun setTexture(event: Event.Texture){
        val entityId = entityMap[event.entityId]
        val textureTypeComponent = textureComponentMapper[entityId]?: run {
            textureComponentMapper.create(entityId)
        }
        textureTypeComponent.textureType = event.textureType
    }

    @BusEvent
    fun setEntityType(event: Event.EntityTypeEvent){
        val entityId = entityMap[event.entityId]
        val entityTypeComponent = entityTypeComponentMapper[entityId]?: run {
            entityTypeComponentMapper.create(entityId)
        }
        entityTypeComponent.entityType = event.entityType
    }

    @BusEvent
    fun setStats(event: Event.Stats){
        val entityId = entityMap[event.entityId]
        val stats = statsComponentMapper[entityId]?: run {
            statsComponentMapper.create(entityId)
        }
        stats.setAllStats(event.stats)
    }

    @BusEvent
    fun setStats(event: Event.Inventory){
        event.inventory.forEach {
            println(it.name)
        }
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
        entityComponentMapper.remove(entityId)
        statsComponentMapper.remove(entityId)
        sizeComponentMapper.remove(entityId)
        angleComponentMapper.remove(entityId)
        positionComponentMapper.remove(entityId)
        entityTypeComponentMapper.remove(entityId)
        textureComponentMapper.remove(entityId)
    }


    override fun initialize() {
        player.entityId = world.create()
        entityComponentMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {
        //processRemove(entityId)
    }


    override fun dispose() {


    }
}