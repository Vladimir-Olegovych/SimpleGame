package ecs.systems.draw

sealed class DrawLayer(val action: () -> Unit) {
    class Entity(action: () -> Unit): DrawLayer(action)
    class Other(action: () -> Unit): DrawLayer(action)
}