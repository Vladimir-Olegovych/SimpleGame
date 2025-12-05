package app.ecs.components

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureComponent: Component() {
    var textureId: Int? = null
    var textureRegion: TextureRegion? = null
}