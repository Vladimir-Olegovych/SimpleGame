package models.textures

object TextureType {

    private val nameToIdMap = mutableMapOf<String, Int>()
    private val idToNameMap = mutableMapOf<Int, String>()
    private val idToSkinMap = mutableMapOf<Int, SkinID>()

    init {
        var currentIndex = 0
        val components = listOf(ITEM, BUTTON, BLOCK, ENTITY, BACKGROUND)
        components.forEach { component ->
            val items = component.getItems()
            val skinId = component.getSkinID()
            items.forEachIndexed { itemIndex, itemName ->
                val globalIndex = currentIndex + itemIndex

                nameToIdMap[itemName] = globalIndex
                idToNameMap[globalIndex] = itemName
                idToSkinMap[globalIndex] = skinId
            }

            currentIndex += items.size
        }
    }

    fun getNameById(id: Int): String {
        return idToNameMap[id]!!
    }

    fun getSkinById(id: Int): SkinID {
        return idToSkinMap[id]!!
    }

    fun getIdByName(name: String): Int {
        return nameToIdMap[name]!!
    }

    object ITEM: TextureTypeComponent() {

        override fun getSkinID(): SkinID = SkinID.ITEM

        override fun getItems(): Array<String> {
            return arrayOf(DIAMOND, IRON_BAR, IRON_LIST)
        }

        const val DIAMOND = "ic_diamond_item"
        const val IRON_BAR = "ic_iron_ingort_item"
        const val IRON_LIST = "ic_iron_list_item"
    }

    object BUTTON: TextureTypeComponent() {

        override fun getSkinID(): SkinID = SkinID.BUTTON

        override fun getItems(): Array<String> {
            return arrayOf(BUTTON_OFF, BUTTON_ON, ITEM_BOX, MENU)
        }

        const val BUTTON_OFF = "ic_button_off"
        const val BUTTON_ON = "ic_button_on"
        const val ITEM_BOX = "ic_item_box"
        const val MENU = "ic_menu_button"
    }

    object BLOCK: TextureTypeComponent() {

        override fun getSkinID(): SkinID = SkinID.BLOCK

        override fun getItems(): Array<String> {
            return arrayOf(ERROR, GRASS, LAVA, STONE, WOOD_PLANKS)
        }

        const val ERROR = "ic_error_texture"
        const val GRASS = "ic_grass_block"
        const val LAVA = "ic_lava_block"
        const val STONE = "ic_stone_block"
        const val WOOD_PLANKS = "ic_wood_planks_block"
    }

    object ENTITY: TextureTypeComponent() {

        override fun getSkinID(): SkinID = SkinID.ENTITY

        override fun getItems(): Array<String> {
            return arrayOf(PLAYER, ZOMBIE)
        }

        const val PLAYER = "ic_player_entity"
        const val ZOMBIE = "ic_zombie_entity"
    }

    object BACKGROUND: TextureTypeComponent() {

        override fun getSkinID(): SkinID = SkinID.BACKGROUND

        override fun getItems(): Array<String> {
            return arrayOf(MENU)
        }

        const val MENU = "ic_menu_background"
    }

}

fun String.asTextureId(): Int = TextureType.getIdByName(this)