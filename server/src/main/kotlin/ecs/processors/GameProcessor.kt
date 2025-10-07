package org.example.ecs.processors

import com.artemis.World

interface GameProcessor {
    fun create(artemisWorld: World)
}