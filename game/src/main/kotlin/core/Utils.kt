package core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import core.textures.SkinID

fun AssetManager.getRegion(skinID: SkinID, name: String): TextureAtlas.AtlasRegion {
    return this.get<TextureAtlas>(skinID.atlas).findRegion(name)
}