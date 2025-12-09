package app.ecs.models

import com.badlogic.gdx.math.MathUtils
import kotlin.math.abs
import kotlin.math.sign

class GlobalAngle(var angle: Float = 0f) {

    companion object {
        const val FULL_CIRCLE = 2f * MathUtils.PI
        const val SECTORS_COUNT = 8
        const val SECTOR_ANGLE = FULL_CIRCLE / SECTORS_COUNT
        const val ROTATION_SPEED = 1.5f
    }

    val currentSector: Int get() = ((angle / FULL_CIRCLE) * SECTORS_COUNT).toInt() % SECTORS_COUNT

    var deltaTime = 0F
    private var targetAngle: Float = angle
    private var isRotating: Boolean = false
    private var rotationDirection: Float = 0f

    private var targetSector: Int? = null
    private var rotationSpeed: Float = ROTATION_SPEED

    fun process() {
        if (!isRotating || deltaTime <= 0) return
        val maxRotation = rotationSpeed * deltaTime

        if (targetSector != null) {
            val targetSectorAngle = targetSector!! * SECTOR_ANGLE
            val angleDiff = calculateShortestAngle(angle, targetSectorAngle)

            if (abs(angleDiff) <= maxRotation) {
                angle = targetSectorAngle
                isRotating = false
                targetSector = null
            } else {
                angle += angleDiff.sign * maxRotation
                angle = normalizeAngle(angle)
            }
        } else {
            angle += rotationDirection * maxRotation
            angle = normalizeAngle(angle)

            if (targetAngle != angle) {
                val angleDiff = calculateShortestAngle(angle, targetAngle)
                if (abs(angleDiff) <= maxRotation) {
                    angle = targetAngle
                    isRotating = false
                }
            }
        }
    }

    fun rotateLeft() {
        rotateToSector((currentSector + 1) % SECTORS_COUNT)
    }

    fun rotateRight() {
        rotateToSector((currentSector - 1 + SECTORS_COUNT) % SECTORS_COUNT)
    }

    fun rotateLeft(sectors: Int) {
        rotateToSector((currentSector + sectors) % SECTORS_COUNT)
    }

    fun rotateRight(sectors: Int) {
        rotateToSector((currentSector - sectors + SECTORS_COUNT) % SECTORS_COUNT)
    }

    fun rotateBySectors(sectors: Int) {
        rotateToSector((currentSector + sectors + SECTORS_COUNT) % SECTORS_COUNT)
    }

    fun rotateToSector(sector: Int) {
        require(sector in 0 until SECTORS_COUNT) { "Sector must be between 0 and ${SECTORS_COUNT - 1}" }

        if (sector == currentSector) return

        targetSector = sector
        isRotating = true
        rotationSpeed = ROTATION_SPEED

        val targetAngle = sector * SECTOR_ANGLE
        val angleDiff = calculateShortestAngle(angle, targetAngle)
        rotationDirection = if (angleDiff > 0) 1f else -1f
    }

    private fun normalizeAngle(angle: Float): Float {
        var normalized = angle % FULL_CIRCLE
        if (normalized < 0) normalized += FULL_CIRCLE
        return normalized
    }

    private fun calculateShortestAngle(from: Float, to: Float): Float {
        var diff = (to - from) % FULL_CIRCLE
        if (diff < -MathUtils.PI) diff += FULL_CIRCLE
        if (diff > MathUtils.PI) diff -= FULL_CIRCLE
        return diff
    }
}