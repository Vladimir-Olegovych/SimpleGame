package app.ecs.models

import com.badlogic.gdx.math.MathUtils
import kotlin.math.abs
import kotlin.math.sign

class GlobalAngle(var angle: Float = 0f) {

    companion object {
        const val FULL_CIRCLE = 2f * MathUtils.PI
        const val SECTORS_COUNT = 8
        const val SECTOR_ANGLE = FULL_CIRCLE / SECTORS_COUNT
        const val ROTATION_SPEED = 3f
    }

    private val listeners = ArrayList<Listener>()
    val currentSector: Int get() = ((angle / FULL_CIRCLE) * SECTORS_COUNT).toInt() % SECTORS_COUNT

    private var targetAngle: Float = angle
    private var isRotating: Boolean = false
    private var rotationDirection: Float = 0f

    private var targetSector: Int? = null
    private var rotationSpeed: Float = ROTATION_SPEED
    private var lastAngleBeforeRotation: Float = angle
    private var wasRotating: Boolean = false

    fun process(deltaTime: Float) {
        if (!isRotating) {
            if (wasRotating) {
                // Завершилось вращение
                notifyRotationEnd()
                wasRotating = false
            }
            return
        }

        // Начало вращения
        if (!wasRotating) {
            lastAngleBeforeRotation = angle
            notifyRotationStart()
            wasRotating = true
        }

        val maxRotation = rotationSpeed * deltaTime
        val oldAngle = angle

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

        if (angle != oldAngle) {
            notifyRotation(angle)
        }

        if (!isRotating) {
            notifyRotationEnd()
            wasRotating = false
        }
    }

    fun rotateLeft() {
        rotateToSector((currentSector - 1 + SECTORS_COUNT) % SECTORS_COUNT)

    }

    fun rotateRight() {
        rotateToSector((currentSector + 1) % SECTORS_COUNT)
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

    /**
     * Запускает вращение на заданный угол в радианах
     */
    fun rotateByAngle(angleRadians: Float) {
        targetAngle = normalizeAngle(angle + angleRadians)
        isRotating = true
        rotationSpeed = ROTATION_SPEED

        val angleDiff = calculateShortestAngle(angle, targetAngle)
        rotationDirection = if (angleDiff > 0) 1f else -1f
        targetSector = null
    }

    /**
     * Вращает к указанному углу в радианах
     */
    fun rotateToAngle(targetAngleRadians: Float) {
        val normalizedTarget = normalizeAngle(targetAngleRadians)

        if (normalizedTarget == angle) return

        targetAngle = normalizedTarget
        isRotating = true
        rotationSpeed = ROTATION_SPEED

        val angleDiff = calculateShortestAngle(angle, targetAngle)
        rotationDirection = if (angleDiff > 0) 1f else -1f
        targetSector = null
    }

    /**
     * Немедленно устанавливает угол без вращения
     */
    fun setAngleImmediate(newAngle: Float) {
        angle = normalizeAngle(newAngle)
        isRotating = false
        targetSector = null
        notifyRotation(angle)
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

    fun isRotating(): Boolean = isRotating

    fun getLastAngleBeforeRotation(): Float = lastAngleBeforeRotation

    /**
     * Добавляет слушателя вращения
     */
    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Удаляет слушателя вращения
     */
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyRotationStart() {
        listeners.forEach { it.onRotationStart(angle, lastAngleBeforeRotation) }
    }

    private fun notifyRotation(newAngle: Float) {
        listeners.forEach { it.onRotate(newAngle) }
    }

    private fun notifyRotationEnd() {
        listeners.forEach { it.onRotationEnd(angle, lastAngleBeforeRotation) }
    }

    interface Listener {
        /**
         * Вызывается в начале вращения
         * @param currentAngle текущий угол в момент начала вращения
         * @param startAngle угол перед началом вращения (предыдущий угол)
         */
        fun onRotationStart(currentAngle: Float, startAngle: Float) {}

        /**
         * Вызывается при каждом изменении угла во время вращения
         * @param angle текущий угол
         */
        fun onRotate(angle: Float) {}

        /**
         * Вызывается в конце вращения
         * @param finalAngle конечный угол
         * @param startAngle угол перед началом вращения
         */
        fun onRotationEnd(finalAngle: Float, startAngle: Float) {}
    }
}