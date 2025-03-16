package ecs.systems

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import body.createCircleEnemy
import body.createWall
import ecs.components.Wall
import ecs.components.Zombie
import kotlin.random.Random

class PhysicSystem: BaseSystem() {

    private lateinit var zombies: ComponentMapper<Zombie>
    private lateinit var walls: ComponentMapper<Wall>
    private val box2dWold = World(Vector2(0F, -98F), true)

    override fun initialize() {
        for (i in 0 until 1000) {
            val entityId = world.create()
            val zombie = zombies.create(entityId)
            zombie.radius = 1F
            zombie.body = box2dWold.createCircleEnemy(
                Random.nextInt(0, 100).toFloat(), Random.nextInt(0, 1000).toFloat(), zombie.radius
            )
        }

        for (i in 0 until 40) {
            val entityId = world.create()
            val wall = walls.create(entityId)
            wall.halfWidth = 5F
            wall.halfHeight = 5F
            wall.body = box2dWold.createWall(i * wall.halfWidth, 0F, wall.halfWidth, wall.halfHeight)
        }
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 8)
    }
}