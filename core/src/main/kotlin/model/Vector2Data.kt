package model

import com.badlogic.gdx.math.Vector2

data class Vector2Data(val x: Float = 0F, val y: Float = 0F) {
     fun toVector2(): Vector2 = Vector2(x, y)
 }