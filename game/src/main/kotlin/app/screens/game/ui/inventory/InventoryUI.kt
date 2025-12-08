package app.screens.game.ui.inventory

import app.ecs.components.InventoryComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import core.textures.TextureStorage
import core.ui.UIComponent
import models.textures.SkinID
import models.textures.TextureType
import models.textures.asTextureId
import tools.graphics.drawable.ColorDrawable
import kotlin.math.ceil

class InventoryUI: UIComponent() {

    companion object {
        const val INVENTORY_SIZE = 32
        const val INVENTORY_VISIBLE_SIZE = 8
        const val INVENTORY_WINDOW_COLS = 8
        const val BOX_SIZE = 40f
        const val BOX_PADDING = 2f
    }

    var currentInventoryId = 0
    private var isWindowVisible = false

    @Wire
    private lateinit var stage: Stage
    @Wire
    private lateinit var assetManager: AssetManager
    @Wire
    private lateinit var textureStorage: TextureStorage

    private lateinit var inventoryWindow: Window
    private lateinit var windowInventoryTable: Table
    private lateinit var windowInventoryBoxes: Array<Table>
    private lateinit var windowInventoryCells: Array<Container<Table>>

    private lateinit var bottomInventoryTable: Table
    private lateinit var bottomInventoryBoxes: Array<Table>
    private lateinit var bottomInventoryCells: Array<Container<Table>>

    private lateinit var itemImages: MutableMap<Int, Image>
    private lateinit var countLabels: MutableMap<Int, Label>
    private lateinit var labelStyle: Skin

    private lateinit var inventoryComponentMapper: ComponentMapper<InventoryComponent>
    private lateinit var dragAndDrop: DragAndDrop

    override fun initialize() {
        labelStyle = assetManager.get(SkinID.BUTTON.skin)

        itemImages = mutableMapOf()
        countLabels = mutableMapOf()
        dragAndDrop = DragAndDrop()

        initializeBottomInventory()
        initializeInventoryWindow()

        hideInventory()
    }

    private fun initializeBottomInventory() {
        val itemBoxDrawable = textureStorage.getDrawable(TextureType.BUTTON.ITEM_BOX.asTextureId())

        bottomInventoryTable = Table()
        bottomInventoryBoxes = Array(INVENTORY_VISIBLE_SIZE) {
            Table().apply {
                background(itemBoxDrawable)
                addListener { event ->
                    if (event.isHandled) false
                    else {
                        if (event.toString().contains("touchDown")) {
                            onInventorySlotClicked(it, false)
                            true
                        } else false
                    }
                }
                bottomInventoryTable.add(this)
                    .size(BOX_SIZE)
                    .pad(BOX_PADDING)
            }
        }

        bottomInventoryCells = Array(INVENTORY_VISIBLE_SIZE) {
            Container<Table>().apply {
                fill()
                bottomInventoryBoxes[it].add(this)
            }
        }

        getTable().add(bottomInventoryTable)
            .padBottom(8f)
            .bottom()
            .expandY()
    }

    private fun initializeInventoryWindow() {
        val itemBoxDrawable = textureStorage.getDrawable(TextureType.BUTTON.ITEM_BOX.asTextureId())
        val windowDrawable = ColorDrawable(0F, 0F, 0F,1F)

        val windowSkin = assetManager.get<Skin>(SkinID.BUTTON.skin)
        inventoryWindow = Window("Inventory", windowSkin).apply {
            isMovable = true
            isResizable = false
            background(windowDrawable)
            val windowWidth = (BOX_SIZE * INVENTORY_WINDOW_COLS) + (BOX_PADDING * (INVENTORY_WINDOW_COLS - 1)) + 40f
            val rows = ceil(INVENTORY_SIZE.toDouble() / INVENTORY_WINDOW_COLS).toInt()
            val windowHeight = (BOX_SIZE * rows) + (BOX_PADDING * (rows - 1)) + 40f
            setSize(windowWidth, windowHeight)
            isVisible = false
        }

        windowInventoryTable = Table()
        windowInventoryBoxes = Array(INVENTORY_SIZE) {
            Table().apply {
                background(itemBoxDrawable)
                addListener { event ->
                    if (event.isHandled) false
                    else {
                        if (event.toString().contains("touchDown")) {
                            onInventorySlotClicked(it, true)
                            true
                        } else false
                    }
                }
                windowInventoryTable.add(this)
                    .size(BOX_SIZE)
                    .pad(BOX_PADDING)
                if ((it + 1) % INVENTORY_WINDOW_COLS == 0) {
                    windowInventoryTable.row()
                }
            }
        }

        windowInventoryCells = Array(INVENTORY_SIZE) {
            Container<Table>().apply {
                fill()
                windowInventoryBoxes[it].add(this)
            }
        }

        inventoryWindow.add(windowInventoryTable).center().pad(10f)
    }

    fun showInventory() {
        if (isWindowVisible) return
        showInventoryWindow()
    }

    fun hideInventory() {
        if (!isWindowVisible) return
        inventoryWindow.remove()
        inventoryWindow.isVisible = false
        isWindowVisible = false

        bottomInventoryTable.isVisible = true
        updateBottomInventory()
    }

    private fun showInventoryWindow() {
        if (!isWindowVisible) {
            getTable().addActor(inventoryWindow)

            val stageWidth = stage.width
            val stageHeight = stage.height

            val centerX = (stageWidth - inventoryWindow.width) / 2
            val centerY = (stageHeight - inventoryWindow.height) / 2

            inventoryWindow.setPosition(centerX, centerY)

            inventoryWindow.isVisible = true
            isWindowVisible = true

            updateWindowInventory()
            bottomInventoryTable.isVisible = false
        }
    }

    private fun updateBottomInventory() {
        val inventory = inventoryComponentMapper[currentInventoryId] ?: return
        clearBottomInventory()

        for (i in 0 until INVENTORY_VISIBLE_SIZE) {
            updateInventorySlot(i, inventory, bottomInventoryCells[i])
        }
    }

    private fun updateWindowInventory() {
        val inventory = inventoryComponentMapper[currentInventoryId] ?: return
        clearWindowInventory()

        for (i in 0 until INVENTORY_SIZE) {
            updateInventorySlot(i, inventory, windowInventoryCells[i])
        }
    }

    private fun updateInventorySlot(
        slotIndex: Int,
        inventory: InventoryComponent,
        container: Container<Table>
    ) {
        val items = inventory.getSlotItems(slotIndex) ?: return
        if (items.isEmpty()) return

        val itemTable = Table()

        val firstItem = items[0]
        val textureId = firstItem.textureId
        val textureDrawable = textureStorage.getDrawable(textureId)

        val itemImage = Image(textureDrawable).apply {
            setSize(BOX_SIZE * 0.8f, BOX_SIZE * 0.8f)
        }

        itemImages[slotIndex] = itemImage

        itemTable.add(itemImage).center().expand().fill()

        if (items.size > 1) {
            val itemLabel = Label(items.size.toString(), labelStyle, "small").apply {
                setPosition(BOX_SIZE - prefWidth - 4f, 2f)
            }
            countLabels[slotIndex] = itemLabel
            itemTable.addActor(itemLabel)
        }

        container.actor = itemTable
    }

    private fun clearBottomInventory() {
        for (i in 0 until INVENTORY_VISIBLE_SIZE) {
            bottomInventoryCells[i].actor = null
            itemImages.remove(i)?.remove()
            countLabels.remove(i)?.remove()
        }
    }

    private fun clearWindowInventory() {
        for (i in 0 until INVENTORY_SIZE) {
            windowInventoryCells[i].actor = null
            itemImages.remove(i)?.remove()
            countLabels.remove(i)?.remove()
        }
    }

    fun updateInventory() {
        if (isWindowVisible) {
            updateWindowInventory()
        } else {
            updateBottomInventory()
        }
    }

    private fun onInventorySlotClicked(slotIndex: Int, isWindowSlot: Boolean) {
        val inventory = inventoryComponentMapper[currentInventoryId] ?: return
        val items = inventory.getSlotItems(slotIndex)

        if (items != null && items.isNotEmpty()) {
            println("Клик по ячейке ${if (isWindowSlot) "окна" else "нижнего"}: $slotIndex")
            println("Предмет: ${items[0].name}, количество: ${items.size}")
        }
    }

    fun setWindowPosition(x: Float, y: Float) {
        inventoryWindow.setPosition(x, y)
    }

    fun getInventoryWindow(): Window = inventoryWindow

    fun isInventoryWindowVisible(): Boolean = isWindowVisible
}