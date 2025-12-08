package app.entity.translator

import app.ecs.components.*
import app.ecs.models.Player
import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.badlogic.gdx.utils.IntMap
import core.models.settings.ClientPreference
import core.textures.TextureStorage
import event.Event
import tools.eventbus.annotation.BusEvent

class ServerEntityTranslator() {

    private lateinit var world: World

    fun setup(world: World){
        this.world = world
        world.inject(this)
    }

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var textureStorage: TextureStorage
    @Wire
    private lateinit var clientPreference: ClientPreference

    val entityMap = IntMap<Int>()

    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
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
        val entityId = entityMap[event.entityId]?: return
        val entityPosition = positionComponentMapper[entityId]?: positionComponentMapper.create(entityId)

        entityPosition.setPosition(
            event.x * clientPreference.drawScale,
            event.y * clientPreference.drawScale
        )
    }

    @BusEvent
    fun setSize(event: Event.Size){
        val entityId = entityMap[event.entityId]?: return
        val size = sizeComponentMapper[entityId]?: sizeComponentMapper.create(entityId)

        size.radius = event.radius * clientPreference.drawScale
        size.width = event.width * clientPreference.drawScale
        size.height = event.height * clientPreference.drawScale
        size.halfWidth = size.width / 2
        size.halfHeight = size.height / 2
    }

    @BusEvent
    fun setAngle(event: Event.Angle){
        val entityId = entityMap[event.entityId]?: return
        val angle = angleComponentMapper[entityId]?: angleComponentMapper.create(entityId)

        angle.setAngle(event.angle)
    }

    @BusEvent
    fun setTexture(event: Event.Texture){
        val entityId = entityMap[event.entityId]?: return
        val textureTypeComponent = textureComponentMapper[entityId]?: textureComponentMapper.create(entityId)
        val textureId = event.textureId
        val textureRegion = textureStorage.getRegion(textureId)
        textureTypeComponent.textureId = textureId
        textureTypeComponent.textureRegion = textureRegion
    }

    @BusEvent
    fun setEntityType(event: Event.EntityTypeEvent){
        val entityId = entityMap[event.entityId]?: return
        val entityTypeComponent = entityTypeComponentMapper[entityId]?: entityTypeComponentMapper.create(entityId)

        entityTypeComponent.entityType = event.entityType
    }

    @BusEvent
    fun setStats(event: Event.Stats){
        val entityId = entityMap[event.entityId]?: return
        val stats = statsComponentMapper[entityId]?: statsComponentMapper.create(entityId)

        stats.setAllStats(event.stats)
    }

    @BusEvent
    fun setInventory(event: Event.Inventory){
        val entityId = entityMap[event.entityId]?: return
        val inventoryComponent = inventoryComponentMapper[entityId]?: inventoryComponentMapper.create(entityId)
        inventoryComponent.inventorySlots = event.inventory
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
        world.delete(entityId)
    }

    @BusEvent
    fun setCurrentPlayer(event: Event.CurrentPlayer){
        player.serverId = event.entityId
        entityMap.put(event.entityId, player.entityId)
    }

}