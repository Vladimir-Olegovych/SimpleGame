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
    var time = 0F
    var maxTime = 0F

    var dayTime = 0F
    var eveningTime = 0F
    var nightTime = 0F
    var dawnTime = 0F

    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var textureComponentMapper: ComponentMapper<TextureComponent>
    private lateinit var entityTypeComponentMapper: ComponentMapper<EntityTypeComponent>
    private lateinit var entityComponentMapper: ComponentMapper<EntityComponent>
    private lateinit var statsComponentMapper: ComponentMapper<StatsComponent>
    private lateinit var positionComponentMapper: ComponentMapper<PositionComponent>
    private lateinit var sizeComponentMapper: ComponentMapper<SizeComponent>
    private lateinit var angleComponentMapper: ComponentMapper<AngleComponent>
    private lateinit var staticAngleComponent: ComponentMapper<StaticAngleComponent>
    private lateinit var staticPositionComponent: ComponentMapper<StaticPositionComponent>

    @BusEvent
    fun onTime(event: Event.Time){
        time = event.time
    }

    @BusEvent
    fun setEntity(event: Event.Entity){
        val entityComponent: EntityComponent
        if (entityMap[event.entityId] == null) {
            val newId = world.create()
            entityComponent = entityComponentMapper.create(newId)
            entityMap.put(event.entityId, newId)
        } else {
            entityComponent = entityComponentMapper[entityMap[event.entityId]]
        }
    }

    @BusEvent
    fun setPosition(event: Event.Position){
        val entityId = entityMap[event.entityId]?: return
        val positionComponent = positionComponentMapper[entityId]?: positionComponentMapper.create(entityId)

        positionComponent.setPosition(
            event.x,
            event.y
        )
    }

    @BusEvent
    fun setStaticPosition(event: Event.StaticPosition){
        val entityId = entityMap[event.entityId]?: return
        val positionComponent = staticPositionComponent[entityId]?: staticPositionComponent.create(entityId)

        positionComponent.position.x = event.x
        positionComponent.position.y = event.y
    }

    @BusEvent
    fun setAngle(event: Event.Angle){
        val entityId = entityMap[event.entityId]?: return
        val angleComponent = angleComponentMapper[entityId]?: angleComponentMapper.create(entityId)

        angleComponent.setAngle(event.angle)
    }

    @BusEvent
    fun setStaticAngle(event: Event.StaticAngle){
        val entityId = entityMap[event.entityId]?: return
        val angleComponent = staticAngleComponent[entityId]?: staticAngleComponent.create(entityId)

        angleComponent.angle = event.angle
    }

    @BusEvent
    fun setSize(event: Event.Size){
        val entityId = entityMap[event.entityId]?: return
        val sizeComponent = sizeComponentMapper[entityId]?: sizeComponentMapper.create(entityId)

        sizeComponent.radius = event.radius
        sizeComponent.width = event.width
        sizeComponent.height = event.height
        sizeComponent.halfWidth = sizeComponent.width / 2
        sizeComponent.halfHeight = sizeComponent.height / 2
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
        val statsComponent = statsComponentMapper[entityId]?: statsComponentMapper.create(entityId)

        statsComponent.setAllStats(event.stats)
    }

    @BusEvent
    fun setInventory(event: Event.Inventory){
        val entityId = entityMap[event.entityId]?: return
        val inventoryComponent = inventoryComponentMapper[entityId]?: inventoryComponentMapper.create(entityId)
        inventoryComponent.inventorySlots = event.inventory
    }

    @BusEvent
    fun setServerParams(event: Event.ServerParams){
        dayTime = event.dayTime
        eveningTime = event.eveningTime
        nightTime = event.nightTime
        dawnTime = event.dawnTime

        maxTime = dayTime + eveningTime + nightTime + dawnTime
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