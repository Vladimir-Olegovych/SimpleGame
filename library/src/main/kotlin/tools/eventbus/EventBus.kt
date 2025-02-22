package tools.eventbus

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

abstract class EventBus<K>: Executor {

    val lifecycleScope = CoroutineScope(this.asCoroutineDispatcher() + SupervisorJob())
    private val subscribersWithPayloadEvent = ArrayList<Pair<K, SubscribeEventPayload<Any>>>()
    private val subscribersWithEvent = ArrayList<Pair<K, SubscribeEvent>>()

    protected fun update(obj: K, event: Any) {
        for (subscriber in subscribersWithPayloadEvent) {
            if (subscriber.first != obj) continue
            subscriber.second.onEventPayload(event)
        }
    }

    protected fun update(obj: K) {
        for (subscriber in subscribersWithEvent) {
            if (subscriber.first != obj) continue
            subscriber.second.onEvent()
            break
        }
    }

    fun <T: Any> unSubscribe(listener: SubscribeEventPayload<T>){
        for (subscribe in subscribersWithPayloadEvent) {
            if (subscribe.second == listener) {
                subscribersWithPayloadEvent.remove(subscribe)
                break
            }
        }

    }

    fun unSubscribe(listener: SubscribeEvent){
        for (subscribe in subscribersWithEvent) {
            if (subscribe.second == listener) {
                subscribersWithEvent.remove(subscribe)
                break
            }
        }
    }

    fun <T: Any> subscribe(obj: K, listener: SubscribeEventPayload<out T>){
        subscribersWithPayloadEvent.add(Pair(obj, listener as SubscribeEventPayload<Any>))
    }

    fun subscribe(obj: K, listener: SubscribeEvent){
        subscribersWithEvent.add(Pair(obj, listener))
    }

    override fun execute(runnable: Runnable) {
        Gdx.app.postRunnable { runnable.run() }
    }

    interface SubscribeEventPayload<T> {
        fun onEventPayload(event: T)
    }

    interface SubscribeEvent {
        fun onEvent()
    }

}