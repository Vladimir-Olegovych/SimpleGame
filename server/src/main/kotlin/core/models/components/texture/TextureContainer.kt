package core.models.components.texture

import models.textures.SkinID
import models.textures.TextureType

class TextureContainer(val textureId: Int,
                       val textureName: String,
                       val skinID: SkinID
) {
    companion object {
        fun get(textureName: String): TextureContainer {
            val textureId = TextureType.getIdByName(textureName)
            return TextureContainer(
                textureId = textureId,
                textureName = textureName,
                skinID = TextureType.getSkinById(textureId)
            )
        }
    }
}