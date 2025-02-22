package client.datasource.listeners

import client.models.ClientWall
import client.models.ClientZombie

interface OnClientReceive {
    fun receiveBody(array: Array<ClientZombie>) {}
    fun receiveWall(array: Array<ClientWall>) {}
    fun connected() {}
    fun disconnected() {}
    fun error(throwable: Throwable) {}
}