package org.example.app.ecs.components.sender

interface CSender<V> {
    fun fetchSendData(): V
}