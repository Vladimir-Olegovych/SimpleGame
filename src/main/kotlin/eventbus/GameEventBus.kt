package eventbus

import client.datasource.listeners.OnClientReceive
import client.datasource.repository.ClientRepository
import client.models.ClientWall
import client.models.ClientZombie
import client.models.User
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tools.eventbus.EventBus

class GameEventBus: KoinComponent, OnClientReceive, EventBus<Events>() {

    private val clientRepository: ClientRepository by inject()

    fun connect(ip: String, port: Int, user: User){
        clientRepository.setListener(object : OnClientReceive {
            override fun receiveBody(array: Array<ClientZombie>) {
                lifecycleScope.launch { update(Events.ZOMBIE, array) }
            }

            override fun receiveWall(array: Array<ClientWall>) {
                lifecycleScope.launch { update(Events.BLOCK, array) }
            }

            override fun connected() {
                lifecycleScope.launch { update(Events.CONNECTED) }
            }
            override fun disconnected() {
                lifecycleScope.launch { update(Events.DISCONNECT) }
            }

            override fun error(throwable: Throwable) {
                lifecycleScope.launch { update(Events.ERROR) }
            }
        })
        clientRepository.connect(ip, port, user)
    }

    fun clear(){
        clientRepository.disconnect()
    }

}