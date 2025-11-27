package org.example.app.ecs.systems

import alexey.tools.common.collections.IntCollection
import alexey.tools.common.level.Chunk
import alexey.tools.common.level.ChunkManager
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.physics.box2d.World
import org.example.app.ecs.components.PhysicsComponent
import java.util.*

class PhysicsSystem: ChunkManager.Listener, BaseSystem() {

    @Wire private lateinit var box2dWold: World

    private lateinit var physicsComponentMapper: ComponentMapper<PhysicsComponent>

    private val entityEnable = LinkedList<IntCollection>()
    private val entityDisable = LinkedList<IntCollection>()

    override fun onEnable(entities: IntCollection, activators: IntCollection, chunk: Chunk, first: Boolean) {
        if(!first) return
        entityEnable.add(entities)
    }

    override fun onDisable(entities: IntCollection, activators: IntCollection, chunk: Chunk, last: Boolean) {
        if(!last) return
        entityDisable.add(entities)
    }

    override fun begin() {
        val edIterator = entityDisable.iterator()
        while (edIterator.hasNext()){
            val entities = edIterator.next()
            for (entityId in entities){
                val physicsComponent = physicsComponentMapper[entityId]?: continue
                physicsComponent.body?.let {
                    it.isActive = false
                }
            }

            edIterator.remove()
        }
        val eeIterator = entityEnable.iterator()
        while (eeIterator.hasNext()){
            val entities = eeIterator.next()
            for (entityId in entities){
                val physicsComponent = physicsComponentMapper[entityId]?: continue
                physicsComponent.body?.let {
                    it.isActive = true
                }
            }

            eeIterator.remove()
        }
    }

    override fun processSystem() {
        box2dWold.step(world.delta, 8, 3)
    }

}