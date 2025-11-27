package app.ecs.components

import com.artemis.Component
import com.badlogic.gdx.Gdx
import kotlin.math.exp

class AngleComponent : Component() {
    private var serverAngle = 0f  // радианы
    private var currentAngle: Float? = null
    private val interpolationSpeed = 15.0f

    fun getServerAngle(): Float = serverAngle

    fun getInterpolatedAngle(): Float {
        val current = currentAngle ?: serverAngle
        val weight = 1 - exp(-interpolationSpeed * Gdx.graphics.deltaTime)

        var diff = serverAngle - current
        while (diff > Math.PI) diff -= (2 * Math.PI).toFloat()
        while (diff < -Math.PI) diff += (2 * Math.PI).toFloat()

        val interpolated = (current + diff * weight) % (2 * Math.PI).toFloat()

        currentAngle = if (interpolated < 0) interpolated + (2 * Math.PI).toFloat() else interpolated
        return currentAngle!!
    }

    fun setAngle(angle: Float) {
        serverAngle = angle % (2 * Math.PI).toFloat()
    }
}
