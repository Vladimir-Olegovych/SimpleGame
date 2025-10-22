package core.textures

enum class SkinID {

    BUTTON,
    BLOCK,
    BACKGROUND;

    val skin = "images/${name.lowercase()}.skin"
    val atlas = "images/${name.lowercase()}.atlas"
}