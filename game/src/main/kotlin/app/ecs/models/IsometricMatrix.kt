package app.ecs.models

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Matrix4

class IsometricMatrix(private val camera: OrthographicCamera) {

    companion object {
        const val MATRIX_Y_SCALE = 0.8F
        val ISO_180_DEG = Matrix4().apply {
            this.`val`[Matrix4.M00] = 1.0f    // X scale
            this.`val`[Matrix4.M01] = 0.0f    // X shear
            this.`val`[Matrix4.M02] = 0.0f    // X for Z
            this.`val`[Matrix4.M03] = 0.0f

            this.`val`[Matrix4.M10] = 0.0f    // Y shear X
            this.`val`[Matrix4.M11] = MATRIX_Y_SCALE   // Y scale
            this.`val`[Matrix4.M12] = 0.0f    // Y shear Z
            this.`val`[Matrix4.M13] = 0.0f

            this.`val`[Matrix4.M20] = 0.0f    // Z shear X
            this.`val`[Matrix4.M21] = 0.0f    // Z shear Y
            this.`val`[Matrix4.M22] = 1.0f    // Z scale
            this.`val`[Matrix4.M23] = 0.0f

            this.`val`[Matrix4.M30] = 0.0f
            this.`val`[Matrix4.M31] = 0.0f
            this.`val`[Matrix4.M32] = 0.0f
            this.`val`[Matrix4.M33] = 1.0f
        }
    }

    val combined = Matrix4()

    fun updateMatrix() {
        combined.set(ISO_180_DEG)
        combined.mul(camera.combined)
    }
}