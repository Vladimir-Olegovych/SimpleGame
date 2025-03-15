package ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import body.createCircleEnemy
import body.createWall
import ecs.components.Physical
import kotlin.random.Random

class PhysicSystem: BaseSystem() {

    private lateinit var physicals: ComponentMapper<Physical>
    private val box2dWold = World(Vector2(0F, -98F), true)

    override fun initialize() {
        for (i in 0 until 100) {
            val entityId = world.create()
            val physical = physicals.create(entityId)
            physical.body = box2dWold.createCircleEnemy(
                Random.nextInt(0, 100).toFloat(), Random.nextInt(0, 1000).toFloat(), 1F
            )
        }

        for (i in 0 until 40) {
            box2dWold.createWall(i * 4F, 0F, 2F, 2F)
        }
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 8)
    }
}