package core.textures

enum class SkinID {

    MAIN,
    BUTTON,
    BLOCK,
    ENTITY,
    BACKGROUND;

    val skin = "images/${name.lowercase()}.skin"
    val atlas = "images/${name.lowercase()}.atlas"
}