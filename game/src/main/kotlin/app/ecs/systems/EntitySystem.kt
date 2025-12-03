package app.ecs.systems

import app.ecs.components.*
import app.ecs.models.Player
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.IntMap
import core.models.settings.ClientPreference
import event.Event
import models.textures.SkinID
import models.textures.TextureType
import tools.eventbus.annotation.BusEvent

@All(EntityComponent::class)
class EntitySystem(): IteratingSystem() {

    @Wire
    private lateinit var player: Player
    @Wire
    private lateinit var assetManager: AssetManager
    @Wire
    private lateinit var clientPreference: ClientPreference

    private val tempTextureMap = IntMap<TextureRegion>()
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
        size.halfWidth = event.halfWidth * clientPreference.drawScale
        size.halfHeight = event.halfHeight * clientPreference.drawScale
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
        val skinID = TextureType.getSkinById(event.textureId)
        val textureName = TextureType.getNameById(event.textureId)

        val texture = tempTextureMap[textureId]?: let {
            val texture = assetManager.get<TextureAtlas>(skinID.atlas).findRegion(textureName)
            tempTextureMap.put(textureId, texture)
            texture
        }

        textureTypeComponent.textureId = textureId
        textureTypeComponent.textureName = textureName
        textureTypeComponent.textureRegion = texture
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


    override fun initialize() {
        player.entityId = world.create()
        entityComponentMapper.create(player.entityId)
    }

    override fun process(entityId: Int) {
        //processRemove(entityId)
    }


    override fun dispose() {
        tempTextureMap.clear()
    }
}