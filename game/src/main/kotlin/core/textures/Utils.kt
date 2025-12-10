package core.textures

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import kotlin.math.sqrt

fun createShadowShape(): Texture {
    val textureSize = 512
    val pixmap = Pixmap(textureSize, textureSize, Pixmap.Format.RGBA8888)

    pixmap.setColor(0f, 0f, 0f, 0f)
    pixmap.fill()

    val center = textureSize / 2
    val radius = textureSize / 2

    pixmap.setColor(0f, 0f, 0f, 0f)
    pixmap.fill()

    for (x in 0 until textureSize) {
        for (y in 0 until textureSize) {
            val dx = (x - center).toFloat()
            val dy = (y - center).toFloat()
            val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (distance <= radius) {
                val normalized = distance / radius
                val alpha = 1.0f - normalized * normalized

                pixmap.setColor(1f, 1f, 1f, alpha)
                pixmap.drawPixel(x, y)
            }
        }
    }

    try {
        return Texture(pixmap)
    } finally {
        pixmap.dispose()
    }
}