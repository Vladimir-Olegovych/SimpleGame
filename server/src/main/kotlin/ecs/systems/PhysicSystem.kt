package org.example.ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.example.body.createCircleEnemy
import org.example.body.createWall
import org.example.ecs.components.Physical
import org.example.ecs.components.Zombie
import kotlin.random.Random

@All(Physical::class, Zombie::class)
class PhysicSystem: BaseSystem() {

    private lateinit var physicals: ComponentMapper<Physical>
    private lateinit var zombies: ComponentMapper<Zombie>
    private val box2dWold = World(Vector2(0F, -10F), true)

    override fun initialize() {
        for (i in 0 until 40) {
            val entityId = world.create()
            val physical = physicals.create(entityId)
            val zombie = zombies.create(entityId)
            zombie.radius = 40F
            physical.body = box2dWold.createCircleEnemy(
                Random.nextInt(0, 100).toFloat(), Random.nextInt(0, 100
                ).toFloat(), radius = 40F)
        }

        for (i in 0 .. 100) {
            box2dWold.createWall(i * 2F, -600F, 60F, 60F)
        }
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 8)
    }
}