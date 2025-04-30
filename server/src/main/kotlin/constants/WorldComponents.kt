package org.example.constants

import com.badlogic.gdx.physics.box2d.World


object WorldComponents {
    private var box2dWorld: World? = null

    fun getBox2dWorld(): World {
        return box2dWorld!!
    }
    fun setBox2dWorld(box2dWorld: World?){
        this.box2dWorld = box2dWorld
    }
}