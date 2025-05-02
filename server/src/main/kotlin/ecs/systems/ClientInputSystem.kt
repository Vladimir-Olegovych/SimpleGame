package org.example.ecs.systems

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import ecs.components.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.example.ecs.features.SendEntityFeature

@All(Client::class)
class ClientInputSystem(private val lifecycleScope: CoroutineScope): IteratingSystem() {

    private lateinit var clientMapper: ComponentMapper<Client>
    private val tasks = ArrayList<Deferred<Unit>>()

    override fun initialize() {
        SendEntityFeature.initialize(world)
    }

    override fun begin() {
        runBlocking {
            tasks.forEach { it.await() }
            tasks.clear()
        }
    }

    override fun process(entityId: Int) {
        SendEntityFeature.notify(entityId)

        val client = clientMapper[entityId]
        client.getEvents().forEach { event ->
            tasks.add(
                lifecycleScope.async<Unit> {
                    try {
                        client.connection?.sendTCP(event)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            )
        }
        client.clearEvents()
    }
}

