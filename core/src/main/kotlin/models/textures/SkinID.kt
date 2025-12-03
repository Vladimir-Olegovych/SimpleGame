package models.textures

enum class SkinID {

    ITEM,
    BUTTON,
    BLOCK,
    ENTITY,
    BACKGROUND;

    val skin = "images/${name.lowercase()}/${name.lowercase()}.skin"
    val atlas = "images/${name.lowercase()}/${name.lowercase()}.atlas"
}