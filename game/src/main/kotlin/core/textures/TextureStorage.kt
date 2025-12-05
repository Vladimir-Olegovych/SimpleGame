package core.textures

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.IntMap
import models.textures.TextureType

class TextureStorage(private val assetManager: AssetManager) {

    private val tempTextureMap = IntMap<TextureRegion>()

    fun getDrawable(textureId: Int): TextureRegionDrawable {
        return TextureRegionDrawable(getRegion(textureId))
    }

    fun getRegion(textureId: Int): TextureRegion {
        val skinID = TextureType.getSkinById(textureId)
        val textureName = TextureType.getNameById(textureId)
        return tempTextureMap[textureId]?: let {
            val texture = assetManager.get<TextureAtlas>(skinID.atlas).findRegion(textureName)
            tempTextureMap.put(textureId, texture)
            texture
        }
    }


    fun clear(){
        tempTextureMap.clear()
    }
}