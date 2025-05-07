package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.example.ecs.features.EventFeature

@All(Client::class)
class ClientSystem(private val lifecycleScope: CoroutineScope): IteratingSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private val tasks = ArrayList<Deferred<Unit>>()

    override fun initialize() {
        EventFeature.initialize(world)
    }

    override fun process(entityId: Int) {
        EventFeature.notify(entityId)

        val client = clientMapper[entityId]?: return
        client.getEvents().forEach { event ->
            tasks.add(lifecycleScope.async<Unit> {
                try {
                    client.connection?.sendTCP(event)
                } catch (_: Throwable) {}
            })
        }
        client.clearEvents()
    }

    override fun end() {
        runBlocking {
            tasks.forEach { it.await() }
            tasks.clear()
        }
    }
}

