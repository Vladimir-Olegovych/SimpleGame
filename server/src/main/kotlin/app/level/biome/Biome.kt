package app.level.biome

import models.textures.TextureType

enum class Biome(
    val block: String
) {
    DESERT(TextureType.BLOCK.LAVA),
    FOREST(TextureType.BLOCK.GRASS),
    OCEAN(TextureType.BLOCK.STONE)
}